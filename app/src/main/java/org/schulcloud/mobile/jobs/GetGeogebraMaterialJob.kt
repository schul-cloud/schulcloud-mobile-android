package org.schulcloud.mobile.jobs

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.content.GeogebraMaterial
import org.schulcloud.mobile.models.content.GeogebraResponse
import org.schulcloud.mobile.utils.MIME_APPLICATION_JSON

class GetGeogebraMaterialJob(private val materialId: String, callback: RequestJobCallback? = null) :
        RequestJob(callback) {
    companion object {
        val TAG: String = GetGeogebraMaterialJob::class.java.simpleName

        private const val GEOGEBRA_API = "http://www.geogebra.org/api/json.php"
        // language=json
        private const val GEOGEBRA_REQUEST = """{ "request": {
  "-api": "1.0.0",
  "task": {
    "-type": "fetch",
    "fields": {
      "field": [
        { "-name": "preview_url" }
      ]
    },
    "filters" : {
      "field": [
        { "-name":"id", "#text":"%s" }
      ]
    },
    "limit": { "-num": "1" }
  }
}
}"""

        private val HTTP_CLIENT = OkHttpClient()
        private val GSON = GsonBuilder().create()
    }

    override suspend fun onRun() {
        val response = HTTP_CLIENT.newCall(Request.Builder().url(GEOGEBRA_API)
                .post(RequestBody.create(MediaType.parse(MIME_APPLICATION_JSON),
                        GEOGEBRA_REQUEST.format(materialId)))
                .build()).execute()
        if (response.body() == null) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "Error while fetching preview url for Geogebra material $materialId")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
            return
        }

        val parsed = GSON.fromJson(response.body()?.charStream(), GeogebraResponse::class.java)
        if (parsed?.responses?.response?.item?.previewUrl == null) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "Error while parsing preview url for Geogebra material $materialId")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
            return
        }

        val material = GeogebraMaterial().apply {
            id = materialId
            previewUrl = parsed.responses?.response?.item?.previewUrl
        }
        if (BuildConfig.DEBUG)
            Log.i(TAG, "Preview url for Geogebra material $materialId received")
        Sync.SingleData.with(GeogebraMaterial::class.java, material, materialId).run()
    }
}
