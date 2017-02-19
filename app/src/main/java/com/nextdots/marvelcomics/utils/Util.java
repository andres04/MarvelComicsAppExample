package com.nextdots.marvelcomics.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by solerambp01 on 26/10/16.
 */

public class Util {


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final double IMAGE_MAX_SIZE = 700;


    public static void showMessageDialog(String message, Context context) {
        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Los Portales");
            builder.setMessage(message);
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setCancelable(false);

            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (final Exception e) {
            Log.d(Util.class.getSimpleName(), "showAlertDialog(): Failed.", e);
        }
    }

    public static void showDialog(Context context, String message, DialogInterface.OnClickListener aceptarListener){
        try {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(context);
            builder.setTitle("Los Portales");
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton("Aceptar", aceptarListener);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }catch (Exception e){

        }
    }

    private static Gson gson = new Gson();

    public static Gson getGson(){
        return gson;
    }

    public static String convertUrl(String url){

        if (!url.startsWith("https://") && !url.startsWith("http://")){
            url = "http://" + url;
        }

        return url;
    }




    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float dpToPx(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float pxToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }



    public final static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }



    public static String isNull(String text){
        if(text == null)
            return  "";
        return text;
    }

    public static String isNullShowLine(String text){
        if(text == null)
            return  "-";
        return text;
    }


    public static boolean containsNumber(String text){
        return text.matches(".*[0-9].*");
    }


    public static boolean containsLetter(String text){
        return text.matches(".*[a-zA-Z]+.*");
    }


    public static File getOutputMediaFileUri(int type){
        return getOutputMediaFile(type);
    }


    private static File getOutputMediaFile(int type){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "FitAdvisor");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }


        return mediaFile;
    }


    public static String getUrlProfileFacebook(String id){
        return "https://graph.facebook.com/" + id + "/picture?type=large";
    }




    public static boolean isEmpty(String texto){
        if(texto == null)
            return true;

        return texto.length() == 0;
    }


    public static void openBrowser(String url, Activity activity){

        try {

            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;


            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            activity.startActivity(i);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Context context, EditText field) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(field.getWindowToken(), 0);
    }

    public static void showKeyboard(Context context, EditText editText){
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static boolean isGPSEnabled (Context mContext){
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {

            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            throw e;
        }
        finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }


        return type;
    }


    public static void shareLink(Context context, String text) {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);

            sendIntent.setType("text/plain");
            context.startActivity(sendIntent);
        }catch (Exception e){

        }
    }


    public static Bitmap resizeImage(File file){
        try {

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            return selectedBitmap;

        }catch (Exception e){
            return  null;
        }
    }


    public static File resizeFile(File file){

        try {
            Bitmap selectedBitmap = decodeFile(file);
            File newFile =  getOutputMediaFile(Util.MEDIA_TYPE_IMAGE);
            FileOutputStream outputStream = new FileOutputStream(newFile);
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);
            outputStream.flush();
            outputStream.close();

            return  newFile;

        }catch (Exception e){
            return null;
        }

    }


    private static Bitmap decodeFile(File f){

        Bitmap b = null;

        try {

            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int)Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();

        }catch (Exception e){

        }

        return b;
    }



    public static boolean isNetworkConnected(Context context){
        boolean isConnected;
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null)
            isConnected = activeNetwork.isConnectedOrConnecting();
        else
            isConnected = false;

        if(!isConnected)
            Util.showMessageDialog("En estos momentos no cuentas con conexión a internet. Por favor, inténtalo más tarde.", context);

        return isConnected;
    }


//    public static void makeAPhoneCall(Context context, String numero) throws SecurityException{
//        String uri = "tel:" + numero;
//        Intent intent = new Intent(Intent.ACTION_CALL);
//        intent.setData(Uri.parse(uri));
//        context.startActivity(intent);
//    }
//
//
//


}
