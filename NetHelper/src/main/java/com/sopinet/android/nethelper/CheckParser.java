package com.sopinet.android.nethelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class CheckParser {
    public MinimalJSON data = null;

    public Boolean parse(String json)
    {

        final Type tipoEnvoltorioCPD = new TypeToken<MinimalJSON>(){}.getType();
        final Gson gson = new Gson();
        MinimalJSON envoltorioCPD = null;
        try {
            envoltorioCPD = gson.fromJson(json, tipoEnvoltorioCPD);
        } catch(Exception e) {
            return null;
        }

        if (envoltorioCPD.state.equals("1")) {
            return true;
        } else {
            return false;
        }
    }
}
