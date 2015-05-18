package mx.com.factico.diputinder.parser;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import mx.com.factico.diputinder.beans.Candidatos;
import mx.com.factico.diputinder.beans.Diputado;
import mx.com.factico.diputinder.dialogues.Dialogues;

public class GsonParser {
	private static String TAG_CLASS = GsonParser.class.getName();

	public static Diputado getDiputadoFromJSON(String json) throws Exception {
		Gson gson = new Gson();

		return gson.fromJson(json, Diputado.class);
	}

    public static List<Diputado> getListDiputadosFromJSON(String json) {
        Gson gson = new Gson();

        Type listType = new TypeToken<List<Diputado>>(){}.getType();

        return gson.fromJson(json, listType);
    }

	public static Candidatos getCandidatosFromJSON(String json) {
		Gson gson = new Gson();

		return gson.fromJson(json, Candidatos.class);
	}

	public static String createJsonFromObject(Object object) {
		Gson gson = new Gson();
		String json = gson.toJson(object);
		
		Dialogues.Log(TAG_CLASS, "Json: " + json, Log.INFO);
		
		return json;
	}
	
	public static String createJsonFromObjectWithoutExposeAnnotations(Object object) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(object);
		
		Dialogues.Log(TAG_CLASS, "Json: " + json, Log.INFO);
		
		return json;
	}
}
