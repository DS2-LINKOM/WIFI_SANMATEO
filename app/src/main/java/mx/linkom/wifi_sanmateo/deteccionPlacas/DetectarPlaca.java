package mx.linkom.wifi_sanmateo.deteccionPlacas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;

public class DetectarPlaca {


    public static Bitmap reconocerPlaca(Bitmap bitmap, objectDetectorClass objectDetectorClass, int rotarImagen) {

        if (rotarImagen == 1) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);

            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            bitmap = rotatedBitmap;
        }
        //Convertir bitmap image a Mat image
        //CV_8UC4: RGBA image
        //CV_8UC1: Grayscale image
        Mat selected_image = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, selected_image);

        //Pasar la imagen al metodo para reeconocer placa
        selected_image = objectDetectorClass.recognizePhoto(selected_image);
        //Convertir mat image a bitmap
        Bitmap bitmap1 = null;
        bitmap1 = Bitmap.createBitmap(selected_image.cols(), selected_image.rows(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(selected_image, bitmap1);

        return bitmap1;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Bitmap fechaHoraFoto(Bitmap foto){

//        Matrix matrix = new Matrix();
//        matrix.postRotate(90);
//
//        Bitmap rotatedBitmap = Bitmap.createBitmap(foto, 0, 0, foto.getWidth(), foto.getHeight(), matrix, true);
//
//        foto = rotatedBitmap;

        Log.e("foto", "Método fechahora" );
        Mat selected_image = new Mat(foto.getHeight(), foto.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(foto, selected_image);

        // Define the text to be drawn
        LocalDateTime hoy = LocalDateTime.now();

        int year = hoy.getYear();
        int month = hoy.getMonthValue();
        int day = hoy.getDayOfMonth();
        int hour = hoy.getHour();
        int minute = hoy.getMinute();
        int second = hoy.getSecond();

        String fecha = "";

        //Poner el cero cuando el mes o dia es menor a 10
        if (day < 10 || month < 10) {
            if (month < 10 && day >= 10) {
                fecha = year + "/0" + month + "/" + day;
            } else if (month >= 10 && day < 10) {
                fecha = year + "/" + month + "/0" + day;
            } else if (month < 10 && day < 10) {
                fecha = year + "/0" + month + "/0" + day;
            }
        } else {
            fecha = year + "-" + month + "-" + day;
        }

        String hora = "";

        if (hour < 10 || minute < 10) {
            if (hour < 10 && minute >= 10) {
                hora = "0" + hour + ":" + minute;
            } else if (hour >= 10 && minute < 10) {
                hora = hour + ":0" + minute;
            } else if (hour < 10 && minute < 10) {
                hora = "0" + hour + ":0" + minute;
            }
        } else {
            hora = hour + ":" + minute;
        }

        String segundos = "00";

        if (second < 10) {
            segundos = "0" + second;
        } else {
            segundos = "" + second;
        }

        //String text = fecha + " " + hora;


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String formattedDate = dateFormat.format(calendar.getTime());

        Log.e("FECHA", formattedDate);
        Log.e("FECHA", "Tamaño: " + formattedDate.length() + " menos 5: " + (formattedDate.length() - 6));

        String fechahora = "";

        for (int i = 0; i < formattedDate.length(); i++) {
            char c = formattedDate.charAt(i);

            if (i > (formattedDate.length() - 6)){

                fechahora += (Character.isWhitespace(c) || !Character.isLetterOrDigit(c) && c != '.')? "" : c;

//                if (Character.isWhitespace(c) || !Character.isLetterOrDigit(c) && c != '.'){
//                }else {
//                    fechahora += formattedDate.charAt(i);
//                }
            }else {
                fechahora += formattedDate.charAt(i);
            }

        }

        String text = fechahora;

        // Define the font face, scale, color, thickness, and line type
        int fontFace = Core.FONT_HERSHEY_SIMPLEX;
        double fontScale = 5.0;
        Scalar color = new Scalar(255, 0, 0); // White color
        int thickness = 25;
        int lineType = Imgproc.LINE_AA;

        // Define the text's baseline anchor point
        Point org = new Point(50, 300);
        Point org2 = new Point(50, 550);
        Point org3 = new Point(50, 800);

        // Draw the text on the image

        Imgproc.putText(selected_image, "LINK WIFI", org, fontFace, fontScale, color, thickness, lineType, false);

        Imgproc.putText(selected_image, fecha, org2, fontFace, fontScale, color, thickness, lineType, false);

        Imgproc.putText(selected_image, hora, org3, fontFace, fontScale, color, thickness, lineType, false);




        Bitmap bitmap =Bitmap.createBitmap(selected_image.cols(),selected_image.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(selected_image,bitmap);

        // escribir texto en el marco
        // cadena de clase nombre del objeto // punto de partida // color del texto // tamaño del texto
        //Imgproc.putText(foto,labelList.get((int) class_value),new Point(left,top),3,1,new Scalar(255, 0, 0, 255),2);

        return bitmap;
    }

    public static String getTextFromImage(Bitmap bitmap, Context context) {
        String placa = "";

        TextRecognizer recognizer = new TextRecognizer.Builder(context).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(context, "Ocurrio un error", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < textBlockSparseArray.size(); i++) {
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }

            Log.e("PlacaCompleta", "" + stringBuilder.toString());

            placa = getLicensePlate(stringBuilder.toString());

            String txtPlaca = "";
            for (char c : placa.toCharArray()) {
                if (!esGuion(c + "")) {
                    txtPlaca += c;
                }
            }

            placa = txtPlaca;

        }
        return placa;
    }


    //------------------------OBTENER SOLO LA PLACA-------------------------------------------------


    //-------------------------------PATRONES DE UNA PLACA------------------------------------------
    //      GK-5631-C
    //      LE-99-914
    //      ULS-914-G
    //      A-479-TGG
    //      Y35-APX
    //      96TUK6
    //      650-ZUX
    //      NVM-41-48
    //      U03-BAF
    //      06-HB-2B
    //
    //----------------------------------------------------------------------------------------------

    public static String getLicensePlate(String input) {
        String licensePlate = "";
        String[] words = input.split("\\s+|,\\s*");
        for (String word : words) {
            if (word.length() >= 5) {
                try {
                    if (word.length() >= 4) {
                        Log.e("menor4", word);
                        if ((isNumbers(word.substring(0, 2)) && esGuion(word.substring(2, 3)) && isUppercaseLetters(word.substring(3, 4)))) { //650-ZUX
                            licensePlate = word;
                            break;
                        }
                    }
                    if (word.length() >= 5) {
                        Log.e("menor5", word);
                        if ((isNumbers(word.substring(0, 2)) && isUppercaseLetters(word.substring(2, 5))) //96TUK6
                                || (isUppercaseLetters(word.substring(0, 3)) && isNumbers(word.substring(3, 4)) && isUppercaseLetters(word.substring(4, 5))) //EMF5S
                                || (isNumbers(word.substring(0, 2)) && esGuion(word.substring(2, 3)) && isUppercaseLetters(word.substring(3, 5))) //650-ZUX
                                || (isUppercaseLetters(word.substring(0, 1)) && isNumbers(word.substring(1, 2)) && isUppercaseLetters(word.substring(2, 5))) //S3ERS
                                || (isUppercaseLetters(word.substring(0, 2)) && esGuion(word.substring(2, 3)) && isNumbers(word.substring(3, 5))) //LE-99-914
                                || (isUppercaseLetters(word.substring(0, 1)) && esGuion(word.substring(1, 2)) && isNumbers(word.substring(2, 5))) //A-479-TGG
                                || (isUppercaseLetters(word.substring(0, 1)) && esGuion(word.substring(1, 2)) && isNumbers(word.substring(2, 5))) //A-479-TGG
                        ){
                            licensePlate = word;
                            break;
                        }
                    }
                    if (word.length() >= 6) {
                        Log.e("menor6", word);
                        if ((isNumbers(word.substring(0, 4)) && isUppercaseLetters(word.substring(4, 5)) && isNumbers(word.substring(5, 6))) //6537E7
                                || (isNumbers(word.substring(0, 3)) && esGuion(word.substring(3, 4)) && isNumbers(word.substring(4, 5)) && isUppercaseLetters(word.substring(5, 6)))//968-7PX
                                || (isUppercaseLetters(word.substring(0, 3)) && esGuion(word.substring(3, 4)) && isNumbers(word.substring(4, 6))) //NVM-41-48
                                || (isNumbers(word.substring(0, 3)) && esGuion(word.substring(3, 4)) && isUppercaseLetters(word.substring(4, 6))) //650-ZUX
                        ){
                            licensePlate = word;
                            break;
                        }
                    }
                    if (word.length() >= 7){
                        Log.e("menor7", word);
                        if ((isUppercaseLetters(word.substring(0, 3)) && esGuion(word.substring(3, 4)) && isNumbers(word.substring(4, 7))) //ULS-914-G
                                || (isUppercaseLetters(word.substring(0, 1)) && isNumbers(word.substring(1, 3)) && esGuion(word.substring(3, 4)) && isUppercaseLetters(word.substring(4, 7))) //Y35-APX
                                || (isUppercaseLetters(word.substring(0, 2)) && esGuion(word.substring(2, 3)) && isNumbers(word.substring(3, 7))) //GK-5631-C
                                || (isNumbers(word.substring(0, 3)) && esGuion(word.substring(3, 4)) && isUppercaseLetters(word.substring(4, 7))) //650-ZUX
                        ) {
                            licensePlate = word;
                            break;
                        }
                    }
                } catch (Exception e) {
                    Log.e("DetectarPlaca", e.toString());
                    e.printStackTrace();
                }
            }

        }
        return licensePlate;
    }

    private static boolean esGuion(String input) {
        boolean car;
        if (input.equals("-")) {
            car = true;
        } else {
            car = false;
        }

        return car;
    }

    private static boolean isUppercaseLetters(String input) {
        for (char c : input.toCharArray()) {
            if (!Character.isUpperCase(c)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNumbers(String input) {
        for (char c : input.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}