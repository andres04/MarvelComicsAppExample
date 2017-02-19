package com.nextdots.marvelcomics.presenters;

import android.app.Activity;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.nextdots.marvelcomics.messages.FacebookMessage;
import com.nextdots.marvelcomics.rest.entity.Perfil;
import com.nextdots.marvelcomics.utils.Constants;
import com.nextdots.marvelcomics.utils.Util;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.Arrays;



public class FacebookPresenter {

    private CallbackManager mCallbackManager;

    public FacebookPresenter(){
        buildCallBackManager();
    }

    private void buildCallBackManager(){
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                requestProfile(loginResult);
            }

            @Override
            public void onCancel() {
                EventBus.getDefault().post(new FacebookMessage(FacebookMessage.ON_CANCEL, null, null));
            }

            @Override
            public void onError(FacebookException error) {
                EventBus.getDefault().post(new FacebookMessage(FacebookMessage.ON_ERROR, null, error));
            }
        });
    }


    private void requestProfile(LoginResult loginResult){

        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                Perfil perfil = new Perfil();
                perfil.setNombre(object.optString("name"));
                perfil.setCorreo(object.optString("email"));
                perfil.setId(object.optString("id"));
                perfil.setTipoSocialNetwork(Constants.FACEBOOK);
                perfil.setSocialNetwork(true);
                perfil.setImg(Util.getUrlProfileFacebook(perfil.getId()));
                EventBus.getDefault().post(new FacebookMessage(FacebookMessage.ON_SUCCESS, perfil, null));
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender");
        request.setParameters(parameters);
        request.executeAsync();

    }


    public void login(Activity activity){
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
    }


    public CallbackManager getCallBackManager(){
        return mCallbackManager;
    }



}
