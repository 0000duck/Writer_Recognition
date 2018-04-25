#include"jni.h"
#include<stdlib.h>

#include "AddProcess.h"
#include "recnum.h"
#include "Thinning.h"
#include<stdio.h>
#include"cn_hxc_imgrecognition_processActivity.h"
#include "CString.h"

#include"android/log.h"
#define LOG_TAG "System.out"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

char* Jstring2CStr(JNIEnv* env, jstring jstr) {
	char* rtn = NULL;
	jclass clsstring = (env)->FindClass("java/lang/String");
	jstring strencode = (env)->NewStringUTF("GB2312");
	jmethodID mid = (env)->GetMethodID(clsstring, "getBytes",
			"(Ljava/lang/String;)[B");
	jbyteArray barr = (jbyteArray) (env)->CallObjectMethod(jstr, mid,
			strencode); // String .getByte("GB2312");
	jsize alen = (env)->GetArrayLength(barr);
	jbyte* ba = (env)->GetByteArrayElements(barr, JNI_FALSE);
	if (alen > 0) {
		rtn = (char*) malloc(alen + 1); //"\0"
		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
	}
	(env)->ReleaseByteArrayElements(barr, ba, 0); //

	return rtn;
}
jstring   CharTojstring(JNIEnv*   env,   char*   str)
{
	jsize   len   =   strlen(str);
	jclass   clsstring   =   (env)->FindClass("java/lang/String");
    jstring   strencode   =   (env)->NewStringUTF("GB2312");
	jmethodID   mid   =   (env)->GetMethodID(clsstring,   "<init>",   "([BLjava/lang/String;)V");
	jbyteArray   barr   =   (env)-> NewByteArray(len);
	(env)-> SetByteArrayRegion(barr,0,len,(jbyte*)str);
	return (jstring)(env)-> NewObject(clsstring,mid,barr,strencode);
}

//extern int PhoneNum_Extraction(int* proImage, int lWidth,int lHeight, char cphone[11]);
JNIEXPORT jstring JNICALL Java_cn_hxc_imgrecognition_processActivity_callint(
		JNIEnv * env, jobject obj, jintArray by1_, jint width, jint height,
		jstring num_, jstring win2_, jstring whi2_, jstring model_,jint flag) {

//	jint *image = env->GetIntArrayElements(jarr, 0);
//	char* cnum = Jstring2CStr(env, num);
//	char* cwin2 = Jstring2CStr(env, win2);
//	char* cwhi2 = Jstring2CStr(env, whi2);
//	char* cmodel=Jstring2CStr(env, model);

    jint *by1 = env->GetIntArrayElements(by1_, NULL);
    const char *num = env->GetStringUTFChars(num_, 0);
    const char *win2 = env->GetStringUTFChars(win2_, 0);
    const char *whi2 = env->GetStringUTFChars(whi2_, 0);
    const char *model = env->GetStringUTFChars(model_, 0);

//   GetMFeature1(by1, width, height, 1);

//	CString  charResult=NULL;
//    char cc[12]={0};
//    LOGI("cc0 =%s %s",cc,"aa");
//    jing_ini_np( num,  win2, whi2, model);
//    LOGI("wid,hei =%d-%d",width, height);
//    PhoneNum_Extraction(by1, width, height,cc,flag);
//    LOGI("cc1 =%s %s",cc,"aa");
//    LOGI("cc=%s %s",cc,"aa");




    env->ReleaseIntArrayElements(by1_, by1, 0);
    env->ReleaseStringUTFChars(num_, num);
    env->ReleaseStringUTFChars(win2_, win2);
    env->ReleaseStringUTFChars(whi2_, whi2);
    env->ReleaseStringUTFChars(model_, model);

    return env->NewStringUTF("12435454");

    //jstring strResult=CharTojstring(env,cc);
	//return strResult;
}

