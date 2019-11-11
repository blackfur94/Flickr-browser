package com.sebastianzabrzyski

import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL


enum class DownloadStatus {
    OK, IDLE, NOT_INITIALIZED, FAILED_OR_EMPTY, PERMISSIONS_ERROR, ERROR
}

private const val TAG = "GetRawData"

class GetRawData(private val listener: OnDownloadCompleted) : AsyncTask<String, Void, String>() {

    private var downloadStatus = DownloadStatus.IDLE


    interface OnDownloadCompleted {
        fun onDownloadCompleted(data:String, status: DownloadStatus)
    }

    override fun onPostExecute(result: String) {
       Log.d(TAG,"onPostExecute called")
        listener.onDownloadCompleted(result, downloadStatus)
    }

    override fun doInBackground(vararg params: String?): String {
        if(params[0] == null) {
            downloadStatus = DownloadStatus.NOT_INITIALIZED
            return "No URL specified"
        }

        try{
            downloadStatus = DownloadStatus.OK
            return URL(params[0]).readText()
        } catch (e: Exception) {
            val errorMessage = when(e) {
                is MalformedURLException -> {
                    downloadStatus = DownloadStatus.NOT_INITIALIZED
                    "doInBackground: Invalid URL ${e.message}"
                }
                is IOException -> {
                    downloadStatus = DownloadStatus.FAILED_OR_EMPTY
                    "doInBackground: IO Exception ${e.message}"
                }
                is SecurityException -> {
                    downloadStatus = DownloadStatus.PERMISSIONS_ERROR
                    "doInBackground: Security Exception ${e.message}"
                } else -> {
                    downloadStatus = DownloadStatus.ERROR
                    "doInBackground: Unknown Error ${e.message}"
                }
            }
            Log.e(TAG,errorMessage)
            return errorMessage
        }


    }
}