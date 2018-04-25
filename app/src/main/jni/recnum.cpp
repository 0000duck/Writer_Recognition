#include "bp.h"
#include "processing.h"
#include "Thinning.h"

//#include "AddProcess.h"
#include<string.h>
//#include"recnum.h"
#include"svm-predict.h"


#include"android/log.h"
#define LOG_TAG "System.out"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

int l[3];
int l_h[3];

struct svm_model* model_p;
void jing_ini_np(const char *num,const char *win2,const char*whi2,const char*model) //印刷体数字识别初始化
		{
//	LOGI("num=%s",num);
//	LOGI("win2=%s",win2);
//	LOGI("whi2=%s",whi2);
	int i;
//	CString path,path1;
////	path=GetModuleDirectory();
//	path="C:\\OCR";
//	path1=path+"\\OCR_NUM\\LV\\num2";
//	int pathlen = path1.GetLength();
//	LPTSTR p = path1.GetBuffer(pathlen);
	r_num(l, num);

	input_weights_p = (double **) malloc(
			(unsigned) ((80 + 1) * sizeof(double *)));
	for (i = 0; i < 80 + 1; i++) {
		input_weights_p[i] = (double *) malloc(
				(unsigned) ((30 + 1) * sizeof(double)));
	}
	hidden_weights_p = (double **) malloc(
			(unsigned) ((30 + 1) * sizeof(double *)));
	for (i = 0; i < 30 + 1; i++) {
		hidden_weights_p[i] = (double *) malloc(
				(unsigned) ((10 + 1) * sizeof(double)));
	}

//	path1=path+"\\OCR_NUM\\LV\\win2.dat";
//	pathlen = path1.GetLength();
//	p = path1.GetBuffer(pathlen);
	//读取权值
	r_weight(input_weights_p, 80, 30, win2);
//	path1=path+"\\OCR_NUM\\LV\\whi2.dat";
//	pathlen = path1.GetLength();
//	p = path1.GetBuffer(pathlen);
	r_weight(hidden_weights_p, 30, 10, whi2);
	//svm
	//svm
	//path1=path+"\\OCR_NUM\\LV\\model14.dat";
	model_p=svm_load_model(model);

}

int recnum_jing(int* ImageBuffer, int width, int height) {
//	LOGI("l[0]=%d",l[0]);
//	LOGI("l[1]=%d",l[1]);
//	LOGI("l[2]=%d",l[2]);
	int image[80 * 96];
	int pixel;
	int i, j, m, n;
	int result;
	result = 10;
//	LOGI("width=%d",width);
	CRect m_charRect;
	CRect charRect; //原字符包围框
	memset(image, 255, 80 * 96 * sizeof(int));
	for (i = 0; i < height; i++) {
		for (j = 0; j < width; j++) {
			image[i * 80 + j] = 255 - ImageBuffer[i * width + j];
		}
	}
	/*	for (i = 0; i < height; i++) {
	 for (j = 0; j < width; j++) {
	 image[(i + 5) * 80 + j + 5] = 255 - ImageBuffer[i * width + j];
	 }
	 }*/
//	width = 80;
//	height = 96;
	int m_lianxushu;
	m_lianxushu = 0;

	RemoveScatterNoise(image, width, height, m_lianxushu);

//	double *fea = new double[84];
	double *fea;
	fea = (double*) malloc(84 * sizeof(double));

	for (j = 0; j < 84; j++)
		fea[j] = 0;

//	return result;
	m_charRect = CharSegment(image, width, height);

//	return result;
	charRect = m_charRect;
	StdDIBbyRect(image, m_charRect, width, height, 16, 32);
	for (m = 0; m < height; m++) {
		for (n = 0; n < width; n++) {

			pixel = image[m * width + n];
			if (pixel == 0)
				image[m * width + n] = 1;

			else
				image[m * width + n] = 0;
		}
	}

	ThinRosenfeld(image, height, width);
	for (m = 0; m < height; m++) {
		for (n = 0; n < width; n++) {

			if (image[m * width + n] == 1)
				image[m * width + n] =  0;

			else
				image[m * width + n] = 255;
		}

	}
	expanding(image, width, height);
	feature5(image, charRect, m_charRect, height, width, fea);
	int n_in = l[0];
	//获得隐层结点数目
	int n_hidden = l[1];
	//获得输出层结点数目
	int n_out = l[2];
//	LOGI("n_in=%d",n_in);

	//根据提取的特征进行样本识别
//	result=0;
	result = CodeRecognize(fea, n_in, n_hidden, n_out);
//	LOGI("222222223.5");
	if (result == 7) {
		if ((double) (charRect.right - charRect.left - 1)
				/ (charRect.bottom - charRect.top - 1) < 0.4)
			result = 1;
		else {
			j = 0;
			for (i = charRect.top; i < charRect.bottom; i++) {
				if (ImageBuffer[i * width + charRect.right - 2] == 0)
					j++;
			}
			if (charRect.bottom - charRect.top - j > 20)
				result = 1;
		}
	}
	if (result == 3 || result == 5) {
		if ((double) (charRect.bottom - charRect.top - 1)
				/ (charRect.right - charRect.left - 1) > 2.5)
			result = 1;
		/*	for(i=charRect.left;i<charRect.right;i++)
		 {
		 m=0;
		 for(j=charRect.top;j<charRect.bottom;j++)
		 {
		 if(ImageBuffer[j*width+i]==255)
		 m++;
		 }
		 if(m>(charRect.bottom-charRect.top)*9/10)
		 {
		 result=1;
		 break;
		 }
		 }*/
	}
	free(fea);
//	LOGI("222222223.7");
//	LOGI("result=%d",result);
	return result;

}
int recnum_jingsvm(int* ImageBuffer,int width,int height)
{
	int image[80*96];
	int pixel;
	int i,j,m,n;
	int result;
	result=-1;
	CRect m_charRect;
	CRect charRect;//原字符包围框
	for(i=0;i<96;i++)
	{
		for(j=0;j<80;j++)
		{
			image[i*80+j]=255-ImageBuffer[i*width+j];
		}
	}
	int m_lianxushu;
	m_lianxushu=0;
	RemoveScatterNoise(image, width,height,m_lianxushu);
	double *fea=new double[84];
	for( j=0;j<84;j++)fea[j]=0;
	m_charRect=CharSegment(image,width, height);

	charRect=m_charRect;
	StdDIBbyRect(image,m_charRect,width, height,16,32);

	for(m=0;m<height;m++)
	{
		for(n=0;n<width;n++)
		{

			pixel=image[m*width+n];
			if(pixel==0)
				image[m*width+n]=1;

			else
				image[m*width+n]=0;
		}
	}

	ThinRosenfeld(image, height, width);
	for(m=0;m<height;m++)
	{
		for(n=0;n<width;n++)
		{


			if(image[m*width+n]==1)
				image[m*width+n]=0;

			else
				image[m*width+n]=255;
		}

	}
	expanding(image,width, height);

	feature5(image,charRect,m_charRect,height,width,fea);

	result=svm_recognition(fea,80,model_p);
	if(result==1)
	{
		if(fea[80]>1.4)
			result=7;
		else if (fea[81]<1)
			result=-1;
	}
	if(result==7)
	{
		if((double)(charRect.right-charRect.left-1)/(charRect.bottom-charRect.top-1)<0.4)
			result=1;
		else
		{
			j=0;
			for(i=charRect.top;i<charRect.bottom;i++)
			{
				if(ImageBuffer[i*width+charRect.right-2]==0)
					j++;
			}
			if(charRect.bottom-charRect.top-j>20)
				result=1;
		}
	}
	delete []fea;
	return result;

}
void jing_ini_nh(char *num_h, char *win_h, char*whi_h) //手写体数字识别初始化
		{
	int i;
//	CString path,path1;
////	path=GetModuleDirectory();
//	path="C:\\OCR";
//	path1=path+"\\OCR_NUM\\LV\\num_h";
//	int pathlen = path1.GetLength();
//	LPTSTR p = path1.GetBuffer(pathlen);
	r_num(l_h, num_h);

	input_weights_h = (double **) malloc(
			(unsigned) ((80 + 1) * sizeof(double *)));
	for (i = 0; i < 80 + 1; i++) {
		input_weights_h[i] = (double *) malloc(
				(unsigned) ((30 + 1) * sizeof(double)));
	}
	hidden_weights_h = (double **) malloc(
			(unsigned) ((30 + 1) * sizeof(double *)));
	for (i = 0; i < 30 + 1; i++) {
		hidden_weights_h[i] = (double *) malloc(
				(unsigned) ((10 + 1) * sizeof(double)));
	}

//	path1=path+"\\OCR_NUM\\LV\\win_h.dat";
//	pathlen = path1.GetLength();
//	p = path1.GetBuffer(pathlen);
//	//读取权值
	r_weight(input_weights_h, 80, 30, win_h);
//	path1=path+"\\OCR_NUM\\LV\\whi_h.dat";
//	pathlen = path1.GetLength();
//	p = path1.GetBuffer(pathlen);
	r_weight(hidden_weights_h, 30, 10, whi_h);

}
int recnum_jing_h(int* ImageBuffer, int width, int height) {
	int image[80 * 96];
	int pixel;
	int i, j, m, n;
	int result;
	result = 10;
	CRect m_charRect;
	CRect charRect; //原字符包围框
//	for(i=0;i<96;i++)
//	{
//		for(j=0;j<80;j++)
//		{
//			image[i*80+j]=255-ImageBuffer[i*width+j];
//		}
//	}
	memset(image, 255, 80 * 96 * sizeof(int));
	for (i = 0; i < height; i++) {
		for (j = 0; j < width; j++) {
			image[(i + 5) * 80 + j + 5] = 255 - ImageBuffer[i * width + j];
		}
	}
	width = 80;
	height = 96;
	int m_lianxushu;
	m_lianxushu = 0;
	RemoveScatterNoise(image, width, height, m_lianxushu);
	double *fea = new double[84];
	for (j = 0; j < 84; j++)
		fea[j] = 0;
	m_charRect = CharSegment(image, width, height);

	charRect = m_charRect;
	StdDIBbyRect(image, m_charRect, width, height, 16, 32);

	for (m = 0; m < height; m++) {
		for (n = 0; n < width; n++) {

			pixel = image[m * width + n];
			if (pixel == 0)
				image[m * width + n] = 1;

			else
				image[m * width + n] = 0;
		}
	}

	ThinRosenfeld((int*) image, height, width);
	for (m = 0; m < height; m++) {
		for (n = 0; n < width; n++) {

			if (image[m * width + n] == 1)
				image[m * width + n] = (int) 0;

			else
				image[m * width + n] = (int) 255;
		}

	}
	expanding(image, width, height);

	feature5(image, charRect, m_charRect, height, width, fea);
	/*	int l[3];
	 CString path;
	 path=GetModuleDirectory();
	 path+="\\num2";
	 int pathlen = path.GetLength();
	 LPTSTR p = path.GetBuffer(pathlen);
	 if(r_num(l,p)==false)
	 return 0;*/
	int n_in = l_h[0];
	//获得隐层结点数目
	int n_hidden = l_h[1];
	//获得输出层结点数目
	int n_out = l_h[2];
	//根据提取的特征进行样本识别
	result = CodeRecognize_h(fea, n_in, n_hidden, n_out);
	if (result == 7) {
		if ((double) (charRect.right - charRect.left - 1)
				/ (charRect.bottom - charRect.top - 1) < 0.42)
			result = 1;
		else {
			j = 0;
			for (i = charRect.top; i < charRect.bottom; i++) {
				if (ImageBuffer[i * width + charRect.right - 5] == 255)
					j++;
			}
			if (charRect.bottom - charRect.top - j < 6)
				result = 1;
		}
	}
	if (result == 3 || result == 5) {
		if ((double) (charRect.bottom - charRect.top - 1)
				/ (charRect.right - charRect.left - 1) > 2.5)
			result = 1;
		/*	for(i=charRect.left;i<charRect.right;i++)
		 {
		 m=0;
		 for(j=charRect.top;j<charRect.bottom;j++)
		 {
		 if(ImageBuffer[j*width+i]==255)
		 m++;
		 }
		 if(m>(charRect.bottom-charRect.top)*9/10)
		 {
		 result=1;
		 break;
		 }
		 }*/
	}

	delete[] fea;
	return result;

}
