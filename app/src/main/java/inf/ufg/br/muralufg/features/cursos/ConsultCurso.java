package inf.ufg.br.muralufg.features.cursos;


import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import inf.ufg.br.muralufg.model.Curso;

/** Classe responsavel por realizar a
 *  busca de cursos no webservice
 */

public class ConsultCurso extends AsyncTask<Void, Void, List<Curso>> {

    private ConsultCursoSituation listenerSituation;
    private static final String URL_CONNECTION = "https://dl.dropboxusercontent.com/s/mologtlfcosag0n/oportunidades.json?dl=0";
    private List<Curso> cursos = new ArrayList<>();

    public ConsultCurso(ConsultCursoSituation listenerSituation) {
        this.listenerSituation = listenerSituation;
    }

    @Override
    protected List<Curso> doInBackground(Void... params) {

        this.cursos = consultServer();

        return this.cursos;
    }

    private List<Curso> consultServer() {

        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content..

        try {

            URL url = new URL(URL_CONNECTION);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            InputStream stream = conn.getInputStream();

            java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
            String data = s.hasNext() ? s.next() : "";

            try {

                return readCursos(data);

            } catch (JSONException e) {
                Log.d("", "", e);
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            return (List<Curso>) e;
        } catch (Exception e) {
            Log.d("", "", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.d("", "", e);
                }
            }
        }
        return cursos;

    }

    private List<Curso> readCursos(String data) throws JSONException {
        JSONObject reader = new JSONObject(data);

        JSONArray information = reader.getJSONArray("cursos");

        List<Curso> cursosList = new ArrayList<>();

        for (int i = 0; i < information.length(); i++) {

            Curso w = new Curso();
            w.setId(((JSONObject) information.get(i)).getInt("id"));
            w.setNome(((JSONObject) information.get(i)).getString("nome"));

            cursosList.add(w);
        }

        return cursosList;
    }

    @Override
    protected void onPostExecute(List<Curso> cursos) {
        listenerSituation.onConcludeConsultCurso(cursos);
    }

    public interface ConsultCursoSituation {
        void onConcludeConsultCurso(List<Curso> cursos);
    }
}
