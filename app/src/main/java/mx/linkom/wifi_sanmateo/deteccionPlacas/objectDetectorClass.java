package mx.linkom.wifi_sanmateo.deteccionPlacas;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class objectDetectorClass {
    // se utiliza para cargar el modelo y predecir
    private Interpreter interpreter;
    // almacenar todas las etiquetas en el array
    private List<String> labelList;
    private int INPUT_SIZE;
    private int PIXEL_SIZE=3; // for RGB
    private int IMAGE_MEAN=0;
    private  float IMAGE_STD=255.0f;
    // para inicializar la gpu en la aplicación
    private GpuDelegate gpuDelegate;
    private int height=0;
    private  int width=0;

    public objectDetectorClass(AssetManager assetManager, String modelPath, String labelPath, int inputSize) throws IOException {
        INPUT_SIZE=inputSize;
        // usar para definir gpu o cpu // nº de hilos
        Interpreter.Options options=new Interpreter.Options();
        gpuDelegate=new GpuDelegate();
        //options.addDelegate(gpuDelegate);
        options.setNumThreads(4); // set it according to your phone
        // loading model
        interpreter=new Interpreter(loadModelFile(assetManager,modelPath),options);
        // load labelmap
        labelList=loadLabelList(assetManager,labelPath);
    }

    private List<String> loadLabelList(AssetManager assetManager, String labelPath) throws IOException {
        // para almacenar la etiqueta
        List<String> labelList=new ArrayList<>();
        // crear un nuevo lector
        BufferedReader reader=new BufferedReader(new InputStreamReader(assetManager.open(labelPath)));
        String line;
        // bucle a través de cada línea y almacenarlo en labelList
        while ((line=reader.readLine())!=null){
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    private ByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        // para obtener la descripción del archivo
        AssetFileDescriptor fileDescriptor=assetManager.openFd(modelPath);
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startOffset =fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declaredLength);
    }

    // crear nueva función Mat
    public Mat recognizePhoto(Mat mat_image){
        // si no realiza este proceso obtendrá una predicción incorrecta, menos nº de objeto
        // conviértalo ahora en mapa de bits
        Bitmap bitmap=null;
        bitmap=Bitmap.createBitmap(mat_image.cols(),mat_image.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat_image,bitmap);

        height=bitmap.getHeight();
        width=bitmap.getWidth();

        // escalar el mapa de bits al tamaño de entrada del modelo
        Bitmap scaledBitmap=Bitmap.createScaledBitmap(bitmap,INPUT_SIZE,INPUT_SIZE,false);

        // convertir bitmap a bytebuffer como modelo de entrada debe estar en él
        ByteBuffer byteBuffer=convertBitmapToByteBuffer(scaledBitmap);

        // definición de la salida
        // 10: los 10 objetos más detectados
        // 4: allí coordenada en imagen
        //  float[][][]result=new float[1][10][4];
        Object[] input=new Object[1];
        input[0]=byteBuffer;

        Map<Integer,Object> output_map=new TreeMap<>();
        // no vamos a utilizar este método de salida
        // en su lugar creamos un mapa de tres matrices (cajas, puntuación, clases)

        float[][][]boxes =new float[1][10][4];
        // 10: top 10 objetos detectados
        // 4: allí coordenada en imagen
        float[][] scores=new float[1][10];
        // almacena puntuaciones de 10 objetos
        float[][] classes=new float[1][10];
        // almacena la clase del objeto

        // añádelo a object_map;
        output_map.put(1,boxes);
        output_map.put(3,classes);
        output_map.put(0,scores);

        // ahora predecir
        interpreter.runForMultipleInputsOutputs(input,output_map);

        Object value=output_map.get(1);
        Object Object_class=output_map.get(3);
        Object score=output_map.get(0);

        // bucle a través de cada objeto
        // como salida sólo tiene 10 casillas
        for (int i=0;i<10;i++){
            float class_value=(float) Array.get(Array.get(Object_class,0),i);
            float score_value=(float) Array.get(Array.get(score,0),i);
            // definir el umbral de puntuación


            //Aqui definir el umbral de acuerdo a el modelo
            if(score_value>0.5){
                Object box1=Array.get(Array.get(value,0),i);
                // lo multiplicamos por la altura y anchura originales del marco

                float top=(float) Array.get(box1,0)*height;
                float left=(float) Array.get(box1,1)*width;
                float bottom=(float) Array.get(box1,2)*height;
                float right=(float) Array.get(box1,3)*width;

                Log.e("DETECT_PLACA", "Placa detectada");
                // dibujar rectángulo en el marco original // punto inicial // punto final de la caja // color del grosor de la caja
                Imgproc.rectangle(mat_image,new Point(left,top),new Point(right,bottom),new Scalar(0, 255, 0, 255),2);
                // escribir texto en el marco
                // cadena de clase nombre del objeto // punto de partida // color del texto // tamaño del texto
                //Imgproc.putText(rotated_mat_image,labelList.get((int) class_value),new Point(left,top),3,1,new Scalar(255, 0, 0, 255),2);


                try {
                    Rect rect = new Rect(new Point(left,top),new Point(right,bottom));
                    mat_image = new Mat(mat_image, rect);
                }catch (Exception ex){
                    Log.e("Exception", ex.toString());
                }
            }
        }
        //Now for second change go to CameraBridgeViewBase
        return mat_image ;
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer;
        // alguna entrada del modelo debe ser quant=0 para algún quant=1
        // para este quant=0

        int quant=1;
        int size_images=INPUT_SIZE;
        if(quant==0){
            byteBuffer=ByteBuffer.allocateDirect(1*size_images*size_images*3);
        }
        else {
            byteBuffer=ByteBuffer.allocateDirect(4*1*size_images*size_images*3);
        }
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues=new int[size_images*size_images];
        bitmap.getPixels(intValues,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        int pixel=0;

        // algún error
        //now run
        for (int i=0;i<size_images;++i){
            for (int j=0;j<size_images;++j){
                final  int val=intValues[pixel++];
                if(quant==0){
                    byteBuffer.put((byte) ((val>>16)&0xFF));
                    byteBuffer.put((byte) ((val>>8)&0xFF));
                    byteBuffer.put((byte) (val&0xFF));
                }
                else {
                    // paste this
                    byteBuffer.putFloat((((val >> 16) & 0xFF))/255.0f);
                    byteBuffer.putFloat((((val >> 8) & 0xFF))/255.0f);
                    byteBuffer.putFloat((((val) & 0xFF))/255.0f);
                }
            }
        }
        return byteBuffer;
    }
}