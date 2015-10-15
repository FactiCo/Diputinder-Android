package mx.com.factico.diputinder.parser;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import mx.com.factico.diputinder.beans.Candidate;
import mx.com.factico.diputinder.beans.CandidateInfo;
import mx.com.factico.diputinder.beans.Candidatos;
import mx.com.factico.diputinder.beans.GeocoderResult;
import mx.com.factico.diputinder.beans.Messages;
import mx.com.factico.diputinder.beans.Territory;
import mx.com.factico.diputinder.dialogues.Dialogues;

public class GsonParser {
	private static String TAG_CLASS = GsonParser.class.getName();

	public static Candidate getCandidateFromJSON(String json) throws Exception {
		Gson gson = new Gson();

		return gson.fromJson(json, Candidate.class);
	}

    public static List<CandidateInfo> getListCandidatesInfoFromJSON(String json) throws Exception {
        Gson gson = new Gson();

        Type listType = new TypeToken<List<CandidateInfo>>(){}.getType();

        return gson.fromJson(json, listType);
    }

    public static List<Candidate> getListCandidatesFromJSON(String json) throws Exception {
        Gson gson = new Gson();

        Type listType = new TypeToken<List<Candidate>>(){}.getType();

        return gson.fromJson(json, listType);
    }

	public static Candidatos getCandidatosFromJSON(String json) throws Exception {
		Gson gson = new Gson();

		return gson.fromJson(json, Candidatos.class);
	}

	public static GeocoderResult getGeocoderResultFromJSON(String json) throws Exception {
		Gson gson = new Gson();

		return gson.fromJson(json, GeocoderResult.class);
	}

    public static Territory getTerritoryJSON(String json) {
        Gson gson = new Gson();

        return gson.fromJson(json, Territory.class);
    }

	public static Messages getMessagesFromJSON(String json) throws Exception {
		Gson gson = new Gson();

		return gson.fromJson(json, Messages.class);
	}

    public static List<Messages> getListMessagesFromJSON(String json) throws Exception {
        Gson gson = new Gson();

        Type listType = new TypeToken<List<Messages>>(){}.getType();

        return gson.fromJson(json, listType);
    }

	public static String createJsonFromObject(Object object) {
		Gson gson = new Gson();

		return gson.toJson(object);
	}
	
	public static String createJsonFromObjectWithoutExposeAnnotations(Object object) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

		return gson.toJson(object);
	}
}
