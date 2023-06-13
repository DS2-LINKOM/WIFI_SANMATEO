package mx.linkom.wifi_sanmateo;

import android.content.Context;
import android.content.SharedPreferences;

public class Configuracion {

    private final String SHARED_PREFS_FILE = "HMPrefs";
    private final String KEY_EMAIL = "cor";
    private final String KEY_PASS = "con";
    private final String KEY_QR = "cqr";
    private final String KEY_USU = "cusu";
    private final String KEY_ST = "est";
    private final String KEY_STPLACAS = "estplacas";
    private final String KEY_PLACAS = "cplacas";
    private final String KEY_RESI = "cresid";
    private final String KEY_NOMBRE= "usu_nombre";
    private final String KEY_TIPOUSUARIO= "usu_tipousuario";
    private final String KEY_TOKENVIGI= "usu_tokenvigi";
    private final String KEY_NOMBREPLUMA= "usu_nompluma";
    private final String KEY_NOTI= "usu_noti";
    private final String KEY_ENTRADAS= "usu_entrada";
    private final String KEY_SALIDAS= "usu_salida";
    private final String KEY_PERMANECEN= "usu_permanecen";
    private final String KEY_VISITA= "usu_visita";
    private final String KEY_EVENTTO= "usu_evento";
    private final String KEY_ID_VISITA = "usu_id_visita";
    private final String KEY_IDPLUMA = "usu_idpluma";
    private final String KEY_IDVIGILANTE = "usu_idvigi";

    private final String KEY_PIN = "cpin";
    private final String KEY_NOMRE = "cnombrere";
    private final String KEY_BD = "cbd";
    private final String KEY_BDUSU = "cbdusu";
    private final String KEY_BDCON = "cbdcon";
    private final String KEY_MAC = "cbmac";

    private final String KEY_LOGIN = "clog";

    private Context mContext;

    public Configuracion(Context context){
        mContext = context;
    }


    private SharedPreferences getSettings(){
        return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
    }

    //LOGIN

    public String getLOGIN(){
        return getSettings().getString(KEY_LOGIN, null);
    }

    public void setLOGIN(String clog){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_LOGIN, clog );
        editor.commit();
    }

    //MAC
    public String getMAC(){
        return getSettings().getString(KEY_MAC, null);
    }

    public void setMAC(String cbmac){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_MAC, cbmac );
        editor.commit();
    }

    //PIN
    public String getPin(){
        return getSettings().getString(KEY_PIN, null);
    }

    public void setPin(String cpin){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PIN, cpin );
        editor.commit();
    }

    //NOMBRE_RESIDENCIAL
    public String getNomResi(){
        return getSettings().getString(KEY_NOMRE, null);
    }

    public void setNomResi(String cnombrere){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_NOMRE, cnombrere );
        editor.commit();
    }

    //BD
    public String getBd(){
        return getSettings().getString(KEY_BD, null);
    }

    public void setBd(String cbd){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_BD, cbd );
        editor.commit();
    }

    //BD_USUARIO
    public String getBdUsu(){
        return getSettings().getString(KEY_BDUSU, null);
    }

    public void setBdUsu(String cbdusu){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_BDUSU, cbdusu );
        editor.commit();
    }


    //BD_CONTRASEÑA
    public String getBdCon(){
        return getSettings().getString(KEY_BDCON, null);
    }

    public void setBdCon(String cbdcon){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_BDCON, cbdcon );
        editor.commit();
    }


    //ID VISITA
    public String getIdvisita(){
        return getSettings().getString(KEY_ID_VISITA, null);
    }

    public void setIdvisita(String usu_id_visita){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_ID_VISITA, usu_id_visita );
        editor.commit();
    }

    //EVENTO VISITA GRUPAL
    public String getEvento(){
        return getSettings().getString(KEY_EVENTTO, null);
    }

    public void setEvento(String usu_evento){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_EVENTTO, usu_evento );
        editor.commit();
    }

    //VISITA

    public String getVisita(){
        return getSettings().getString(KEY_VISITA, null);
    }

    public void setVisita(String usu_visita){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_VISITA, usu_visita );
        editor.commit();
    }

    //PERMANECEN

    public int getPermanecen(){
        return getSettings().getInt(KEY_PERMANECEN, 0);
    }

    public void setPermanecen(int usu_permanecen){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putInt(KEY_PERMANECEN, usu_permanecen );
        editor.commit();
    }
    //SALIDA

    public int getSalida(){
        return getSettings().getInt(KEY_SALIDAS, 0);
    }

    public void setSalida(int usu_salida){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putInt(KEY_SALIDAS, usu_salida );
        editor.commit();
    }
    //ENTRADA

    public int getEntrada(){
        return getSettings().getInt(KEY_ENTRADAS, 0);
    }

    public void setEntrada(int usu_entrada){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putInt(KEY_ENTRADAS, usu_entrada );
        editor.commit();
    }

    //ID PLUMA

    public String getiDPluma(){
        return getSettings().getString(KEY_IDPLUMA, null);
    }

    public void setiDPluma(String usu_idpluma){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_IDPLUMA, usu_idpluma );
        editor.commit();
    }

    //ID VIGILANTE

    public String getiDVigilante(){
        return getSettings().getString(KEY_IDVIGILANTE, null);
    }

    public void setiDVigilante(String usu_idvigi){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_IDVIGILANTE, usu_idvigi );
        editor.commit();
    }

    //PLUMA

    public String getNombrelPluma(){
        return getSettings().getString(KEY_NOMBREPLUMA, null);
    }

    public void setNombrelPluma(String usu_nompluma){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_NOMBREPLUMA, usu_nompluma );
        editor.commit();
    }

    //TOKEN VIGILANTE

    public String getNoti(){
        return getSettings().getString(KEY_NOTI, null);
    }

    public void setNoti(String usu_noti){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_NOTI, usu_noti );
        editor.commit();
    }
    //TOKEN VIGILANTE

    public String getTokenVigi(){
        return getSettings().getString(KEY_TOKENVIGI, null);
    }

    public void setTokenVigi(String usu_tokenvigi){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_TOKENVIGI, usu_tokenvigi );
        editor.commit();
    }


    //TIPO DE USUARIO

    public String getTipoUsuario(){
        return getSettings().getString(KEY_TIPOUSUARIO, null);
    }

    public void setTipoUsuario(String usu_tipousuario){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_TIPOUSUARIO, usu_tipousuario );
        editor.commit();
    }

    //Nombre
    public String getNombre(){
        return getSettings().getString(KEY_NOMBRE, null);
    }

    public void setNombre(String usu_nombre){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_NOMBRE, usu_nombre );
        editor.commit();
    }

    public String getResid(){
        return getSettings().getString(KEY_RESI, null);
    }

    public void setResid(String cresid){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_RESI, cresid );
        editor.commit();
    }

    public String getUsu(){
        return getSettings().getString(KEY_USU, null);
    }

    public void setUsu(String cusu){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_USU, cusu );
        editor.commit();
    }

    public String getUserEmail(){
        return getSettings().getString(KEY_EMAIL, null);
    }

    public void setUserEmail(String cor){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_EMAIL, cor );
        editor.commit();
    }

    public String getUserPass(){
        return getSettings().getString(KEY_PASS, null);
    }

    public void setUserPass(String con){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PASS, con );
        editor.commit();
    }

    public String getQR(){
        return getSettings().getString(KEY_QR, null);
    }

    public void setQR(String cqr){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_QR, cqr );
        editor.commit();
    }

    public String getST(){
        return getSettings().getString(KEY_ST, null);
    }

    public void setST(String est){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_ST, est );
        editor.commit();
    }

    public String getPlacas(){
        return getSettings().getString(KEY_PLACAS, null);
    }

    public void setPlacas(String cplacas){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PLACAS, cplacas );
        editor.commit();
    }

    public String getSTPlacas(){
        return getSettings().getString(KEY_STPLACAS, null);
    }

    public void setSTPlacas(String estplacas){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_STPLACAS, estplacas );
        editor.commit();
    }



}








