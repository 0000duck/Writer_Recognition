
#include <stdio.h>
#include <math.h> 
#include <time.h>
#include <stdlib.h>
#include"processing.h"

#include"android/log.h"
#define LOG_TAG "System.out"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

#define BIGRND 32767

double drnd();
double dpn1();
double squash(double x);
double *alloc_1d_dbl(int n);
double *alloc_1d_dbl(int n);
double **alloc_2d_dbl(int m, int n);
void bpnn_initialize(int seed);
void bpnn_randomize_weights(double **w, int m, int n);
void bpnn_zero_weights(double **w, int m, int n);
void bpnn_layerforward(double *l1, double *l2, double **conn, int n1, int n2);
void bpnn_output_error(double *delta, double *target, double *output, int nj);
void bpnn_hidden_error(double* delta_h, int nh, double *delta_o, int no, double **who, double *hidden);
void bpnn_adjust_weights(double *delta, int ndelta, double *ly, int nly, double** w, double **oldw, double eta, double momentum);
void w_weight(double **w,int n1,int n2,const char*name);
bool r_weight(double **w,int n1,int n2,const char *name);
void w_num(int n1,int n2,int n3,const char*name);
bool r_num(int *n,const char *name);

void code(int *image ,int lWidth,CRect m_charRect,double *fea);
//void BpTrain(HDIB hDIB,int n_hidden,double min_ex,double momentum,double eta ,int width,int height);
//void CodeRecognize(HDIB hDIB,int width ,int height ,int n_in ,int n_hidden,int n_out);


/*** ����0��1��˫��������� ***/
double drnd()
{
	return ((double) rand() / (double) BIGRND);
}

/*** ����-1.0��1.0֮���˫��������� ***/
double dpn1()
{
	return ((drnd() * 2.0) - 1.0);
}

double squash(double x)
{
	return (1.0 / (1.0 + exp(-x)));
}
/*** ����1ά˫����ʵ������ ***/

double *alloc_1d_dbl(int n)
{
	double *new1;
	
	new1 = (double *) malloc ((unsigned) (n * sizeof (double)));
	if (new1 == NULL) {
		printf("ALLOC_1D_DBL: Couldn't allocate array of doubles\n");
		return (NULL);
	}
	return (new1);
}

/*** ����2ά˫����ʵ������ ***/

double **alloc_2d_dbl(int m, int n)
{
	int i;
	double **new1;
	
	new1 = (double **) malloc ((unsigned) (m * sizeof (double *)));
	if (new1 == NULL) {
		//	printf("ALLOC_2D_DBL: Couldn't allocate array of dbl ptrs\n");
		return (NULL);
	}
	
	for (i = 0; i < m; i++) {
		new1[i] = alloc_1d_dbl(n);
	}
	
	return (new1);
}
float **alloc_2d_dbl_f(int m, int n)
{
	int i;
	float **new1;
	
	new1 = (float **) malloc ((unsigned) (m * sizeof (float *)));
	if (new1 == NULL) {
		//	printf("ALLOC_2D_DBL: Couldn't allocate array of dbl ptrs\n");
		return (NULL);
	}
	
	for (i = 0; i < m; i++) {
		new1[i] = (float *) malloc ((unsigned) (n * sizeof (float)));
	}
	
	return (new1);
}
void free_2d_dbl_f(float ** old1,int m)
{
	int i;
	for (i = 0; i < m; i++)
	{
		free(old1[i]); 
	}
	free(old1);
	
	
}
int **alloc_2d_dbl_i(int m, int n)
{
	int i;
	int **new1;
	
	new1 = (int **) malloc ((unsigned) (m * sizeof (int *)));
	if (new1 == NULL) {
		//	printf("ALLOC_2D_DBL: Couldn't allocate array of dbl ptrs\n");
		return (NULL);
	}
	
	for (i = 0; i < m; i++) {
		new1[i] = (int *) malloc ((unsigned) (n * sizeof (int)));
	}
	
	return (new1);
}
/*** ������������� ***/
void bpnn_initialize(int seed)
{
	//printf("Random number generator seed: %d\n", seed);
	srand(seed);
}

/*** �����ʼ��Ȩֵ ***/
void bpnn_randomize_weights(double **w, int m, int n)
{
	int i, j;
	
	for (i = 0; i <= m; i++) {
		for (j = 0; j <= n; j++) {
			
			w[i][j] = dpn1();
		}
	}
}
/*** 0��ʼ��Ȩֵ ***/
void bpnn_zero_weights(double **w, int m, int n)
{
	int i, j;
	
	for (i = 0; i <= m; i++) {
		for (j = 0; j <= n; j++) {
			w[i][j] = 0.0;
		}
	}
}

/*********ǰ����*********/
void bpnn_layerforward(double *l1, double *l2, double **conn, int n1, int n2)
{
	double sum;
	int j, k;
	
	/*** ������ֵ ***/
	l1[0] = 1.0;
	
	/*** ���ڵڶ����ÿ����Ԫ ***/
	for (j = 1; j <= n2; j++) {
		
		/*** ��������ļ�Ȩ�ܺ� ***/
		sum = 0.0;
		for (k = 0; k <= n1; k++) {
			sum += conn[k][j] * l1[k];
		}
		l2[j] = squash(sum);
	}
}

/* ������ */

void bpnn_output_error(double *delta, double *target, double *output, int nj)
{
	int j;
	double o, t, errsum;
	
	errsum = 0.0;
	for (j = 1; j <= nj; j++) {
		o = output[j];
		t = target[j];
		delta[j] = o * (1.0 - o) * (t - o);
		
	}
	
}

/* ��������� */

void bpnn_hidden_error(double* delta_h, int nh, double *delta_o, int no, double **who, double *hidden)
{
	int j, k;
	double h, sum, errsum;
	
	errsum = 0.0;
	for (j = 1; j <= nh; j++) {
		h = hidden[j];
		sum = 0.0;
		for (k = 1; k <= no; k++) {
			sum += delta_o[k] * who[j][k];
		}
		delta_h[j] = h * (1.0 - h) * sum;
		
	}
	
}

/* ����Ȩֵ */
void bpnn_adjust_weights(double *delta, int ndelta, double *ly, int nly, double** w, double **oldw, double eta, double momentum)
{
	double new_dw;
	int k, j;
	
	ly[0] = 1.0;
	for (j = 1; j <= ndelta; j++) {
		for (k = 0; k <= nly; k++) {
			new_dw = ((eta * delta[j] * ly[k]) + (momentum * oldw[k][j]));
			w[k][j] += new_dw;
			oldw[k][j] = new_dw;
		}
	}
}
/*******����Ȩֵ**********/
void w_weight(double **w,int n1,int n2,char*name)
{
	int i,j;
	double *buffer;
	FILE *fp;
	fp=fopen(name,"wb+");
	buffer=(double*)malloc((n1+1)*(n2+1)*sizeof(double));
	for(i=0;i<=n1;i++)
	{
		for(j=0;j<=n2;j++)
			buffer[i*(n2+1)+j]=w[i][j];
	}
	fwrite((char*)buffer,sizeof(double),(n1+1)*(n2+1),fp);
	fclose(fp);
	free(buffer);
}



/************��ȡȨֵ*************/
bool  r_weight(double **w,int n1,int n2,const char *name)
{
	int i,j;
	double *buffer;
	FILE *fp;
	if((fp=fopen(name,"rb"))==NULL)
	{
		//::MessageBox(NULL,"�޷���ȡȨֵ��Ϣ",NULL,MB_ICONSTOP);
		return (false);
	}
	buffer=(double*)malloc((n1+1)*(n2+1)*sizeof(double));
	fread((char*)buffer,sizeof(double),(n1+1)*(n2+1),fp);
	
	for(i=0;i<=n1;i++)
	{
		for(j=0;j<=n2;j++)
			w[i][j]=buffer[i*(n2+1)+j];
	}
	fclose(fp);
	free(buffer);
	return(true);
}
bool  r_weight_f(float **w,int n1,int n2,const char *name)
{
	int i,j;
	float *buffer;
	FILE *fp;
	if((fp=fopen(name,"rb"))==NULL)
	{
		//::MessageBox(NULL,"�޷���ȡȨֵ��Ϣ",NULL,MB_ICONSTOP);
		return (false);
	}
	buffer=(float*)malloc((n1+1)*(n2+1)*sizeof(float));
	fread((char*)buffer,sizeof(float),(n1+1)*(n2+1),fp);
	
	for(i=0;i<=n1;i++)
	{
		for(j=0;j<=n2;j++)
			w[i][j]=buffer[i*(n2+1)+j];
	}
	fclose(fp);
	free(buffer);
	return(true);
}
bool  r_weight_i(int **w,int n1,int n2,char *name)
{
	int i,j;
	int *buffer;
	FILE *fp;
	if((fp=fopen(name,"rb"))==NULL)
	{
		//::MessageBox(NULL,"�޷���ȡȨֵ��Ϣ",NULL,MB_ICONSTOP);
		return (false);
	}
	buffer=(int*)malloc((n1+1)*(n2+1)*sizeof(int));
	fread((char*)buffer,sizeof(int),(n1+1)*(n2+1),fp);
	
	for(i=0;i<=n1;i++)
	{
		for(j=0;j<=n2;j++)
			w[i][j]=buffer[i*(n2+1)+j];
	}
	fclose(fp);
	free(buffer);
	return(true);
}
/*****������������Ŀ******/
void w_num(int n1,int n2,int n3,char*name)
{
	FILE *fp;
	fp=fopen(name,"wb+");
	int *buffer;
	buffer=(int*)malloc(3*sizeof(int));
	buffer[0]=n1;
	buffer[1]=n2;
	buffer[2]=n3;
	fwrite((char*)buffer,sizeof(int),3,fp);
	fclose(fp);
	free(buffer);
}

/********��ȡ��������Ŀ*********/

bool r_num(int *n,const char *name)
{
	int *buffer;
	FILE *fp;
	buffer=(int *)malloc(3*sizeof(int));
	if((fp=fopen(name,"rb"))==NULL)
	{
		//::MessageBox(NULL,"������",NULL,MB_ICONSTOP);
		return (false);
	}
	fread((char*)buffer,sizeof(int),3,fp);
	n[0]=buffer[0];
	n[1]=buffer[1];
	n[2]=buffer[2];
	fclose(fp);
	free(buffer);
	return(true);
}
int max1(int a,int b){return a>b?a:b;}
void calneighbor2(int* image,int showLen,int showWid,int i,
			 int j,int* g)
{
	int k;
	int a[8];
	int s[8],t[8];

    	a[0]=(image[showLen*(i-1)+j-1]==0)?1:0;
		a[1]=(image[showLen*i+j-1]==0)?1:0;
		a[2]=(image[showLen*(i+1)+j-1]==0)?1:0;
		a[3]=(image[showLen*(i+1)+j]==0)?1:0;
		a[4]=(image[showLen*(i+1)+j+1]==0)?1:0;
		a[5]=(image[showLen*i+j+1]==0)?1:0;
		a[6]=(image[showLen*(i-1)+j+1]==0)?1:0;
		a[7]=(image[showLen*(i-1)+j]==0)?1:0;
	for(k=0;k<8;k++)
	{
		s[k]=a[k%8]+a[(k+1)%8]+a[(k+2)%8];
		t[k]=a[(k+3)%8]+a[(k+4)%8]+a[(k+5)%8]+a[(k+6)%8]+a[(k+7)%8];
	}
	g[0]=max1(abs(5*s[0]-3*t[0]),abs(5*s[4]-3*t[4]));
	g[1]=max1(abs(5*s[1]-3*t[1]),abs(5*s[5]-3*t[5]));
	g[2]=max1(abs(5*s[2]-3*t[2]),abs(5*s[6]-3*t[6]));
	g[3]=max1(abs(5*s[3]-3*t[3]),abs(5*s[7]-3*t[7]));
	
}
void feature5(int *image,CRect charRect,CRect &m_charRect,int showWid,int showLen,double *fea1)
{
	int i,j,k,m;
    int g[4];
	int pixel;
	double a,b;
	a=b=0;
//	CString str,str1;
	for(i=0;i<16;i++)
	{
		
		for(j=m_charRect.top+int(i/4)*8;j<m_charRect.top+8+(int(i/4))*8;j++)
			for(k=m_charRect.left+(i%4)*4;k<m_charRect.left+4+(i%4)*4;k++)
			{
				pixel=image[j*showLen+k];
				if(pixel==0)
					fea1[i*5+4]+=1;
				calneighbor2(image,showLen,showWid,j,k,g);
				
				for(m=0;m<4;m++)
				{
					if(g[m]>=9)
						fea1[i*5+m]+=1;
					
				}
				

			}
	}
	for(i=0;i<8;i++)
	{
		a+=fea1[i*5+4];
		b+=fea1[i*5+44];
	}
	fea1[80]=a/b;
	for(i=0;i<80;i++)
	{
		if(fea1[i]!=0)
			fea1[i]=fea1[i]/32;
	}
	fea1[81]=(double)(charRect.bottom-charRect.top+1)/
		(double)(charRect.right-charRect.left+1);
//	fea1[83]=(double)(charRect.bottom-charRect.top-1);
/*	for(j=m_charRect.top+1;j<m_charRect.top+33;j++)
	{
		for(k=m_charRect.left+1;k<m_charRect.left+33;k++)
		{
			pixel=image[j*showLen+k];
			calneighbor2(image,showLen,showWid,j,k,g);
			if(g[3]<9)g[3]=0;
			else g[3]=1;
			str1.Format("%d ",g[3]);
			str+=str1;
		}
		str+="\r";
	}
	::MessageBox(NULL,str,"ʶ����",NULL);*/

}
void feature_c(int *image,CRect charRect,CRect &m_charRect,int showWid,int showLen,double *fea1)
{
	int i,j,k,m;
    int g[4];
	int pixel;

//	CString str,str1;
	for(i=0;i<16;i++)
	{
		
		for(j=m_charRect.top+int(i/4)*8;j<m_charRect.top+8+(int(i/4))*8;j++)
			for(k=m_charRect.left+(i%4)*4;k<m_charRect.left+4+(i%4)*4;k++)
			{
				pixel=image[j*showLen+k];
				if(pixel==0)
					fea1[i*5+4]+=1;
				calneighbor2(image,showLen,showWid,j,k,g);
				
				for(m=0;m<4;m++)
				{
					if(g[m]>=9)
						fea1[i*5+m]+=1;
					
				}
				

			}
	}

	for(i=0;i<80;i++)
	{
		if(fea1[i]!=0)
			fea1[i]=fea1[i]/32;
	}


}


//ָ�������������֮��Ȩֵ��ָ��
	double** input_weights_p;
	//ָ�������������֮���Ȩֵ��ָ��
	double** hidden_weights_p;
int CodeRecognize(double *data_in, int n_in,int n_hidden,int n_out)
{
	//ѭ������
	int i,j;
	// ָ��ʶ������ָ�� 
	int recognize;

	//ָ����������ݵ�ָ��
	double* input_unites; 
	//ָ���������ݵ�ָ��
	double* hidden_unites;
	//ָ����������ݵ�ָ��
	double* output_unites; 
	//ָ�������������֮��Ȩֵ��ָ��
//	double** input_weights;
	//ָ�������������֮���Ȩֵ��ָ��
//	double** hidden_weights;
	//Ϊ�������ݽṹ�����ڴ�ռ�
	input_unites=(double *) malloc ((unsigned) ((n_in+1) * sizeof (double)));
	hidden_unites=(double *) malloc ((unsigned) ((n_hidden+1) * sizeof (double)));
	output_unites=(double *) malloc ((unsigned) ((n_out+1) * sizeof (double)));
/*	input_weights = (double **) malloc ((unsigned) ((n_in+1) * sizeof (double *)));
	for (i = 0; i < n_in+1; i++)
	{
		input_weights[i] = (double *) malloc ((unsigned) ((n_hidden+1) * sizeof (double)));
	}
	hidden_weights = (double **) malloc ((unsigned) ((n_hidden+1) * sizeof (double *)));
	for (i = 0; i < n_hidden+1; i++)
	{
		hidden_weights[i] = (double *) malloc ((unsigned) ((n_out+1) * sizeof (double)));
	}
	
	
	//��ȡȨֵ
	CString path,path1;
	path=GetModuleDirectory();
	path1=path+"\\win2.dat";
	int pathlen = path1.GetLength(); 
	LPTSTR p = path1.GetBuffer(pathlen);
	//��ȡȨֵ
	if( r_weight(input_weights,n_in,n_hidden,p)==false)
		return 10;
	path1=path+"\\whi2.dat";
	pathlen = path1.GetLength(); 
	p = path1.GetBuffer(pathlen);
	if(r_weight(hidden_weights,n_hidden,n_out,p)==false)
		return 10;*/
	/*if( r_weight(input_weights,n_in,n_hidden,"E:\\jing\\��\\Recognitionp1\\win3.dat")==false)
		return;
	if(r_weight(hidden_weights,n_hidden,n_out,"E:\\jing\\��\\Recognitionp1\\whi3.dat")==false)
		return;*/
	
	
	//����ȡ�������������������͵��������
	for(i=1;i<=n_in;i++)
		input_unites[i]=data_in[i-1];
	int result=0;
	if(data_in[81]<0.5)
		result=30;
//	if(fabs(data_in[83]-data_in[82])>1)
//		result=30;
	if(result==0)
	{
		
		//ǰ�����뼤��
        bpnn_layerforward(input_unites,hidden_unites,
			input_weights_p, n_in,n_hidden);
        bpnn_layerforward(hidden_unites, output_unites,
			hidden_weights_p,n_hidden,n_out);
		
		//��������������ʶ��
		double a,b ;
		a=b=0;
		
		//����ÿһλ�����
		for(i=1;i<=n_out;i++)
		{
			//�������0.5��Ϊ1
			if(output_unites[i]>a)
			{
				a=output_unites[i];
				j=i;
			}
		}
		for(i=1;i<=n_out;i++)
		{
			if(i!=j)
				if(output_unites[i]>b)
					b=output_unites[i];
		}
		if(a-b>0.05)
			result=j-1;
		else
			result=20;
	}

	//����ж��Ľ��С�ڵ���9����Ϊ����
//	LOGI("result=%d",result);

	if(result==1)
	{
		if(data_in[80]>1.4)
			result=7;
	}
	if(result<=9)
		recognize=result;
	//����ж��Ľ������9����Ϊ�����������λΪһ������ֵ20
	if(result==20)
		recognize=10;
	if(result==30)
		recognize=10;
	
	free(input_unites);
	free(hidden_unites);
	free(output_unites);
/*	for (i = 0; i < n_in+1; i++)
	{
		free(input_weights[i]); 
	}
	
	for (i = 0; i < n_hidden+1; i++)
	{
		free(hidden_weights[i]);
	}
	free(input_weights);
	free(hidden_weights);*/
	return recognize;
}

//ָ�������������֮��Ȩֵ��ָ��
	double** input_weights_h;
	//ָ�������������֮���Ȩֵ��ָ��
	double** hidden_weights_h;
int CodeRecognize_h(double *data_in, int n_in,int n_hidden,int n_out)
{
	//ѭ������
	int i,j;
	// ָ��ʶ������ָ�� 
	int recognize;

	//ָ����������ݵ�ָ��
	double* input_unites; 
	//ָ���������ݵ�ָ��
	double* hidden_unites;
	//ָ����������ݵ�ָ��
	double* output_unites; 
	//ָ�������������֮��Ȩֵ��ָ��
//	double** input_weights;
	//ָ�������������֮���Ȩֵ��ָ��
//	double** hidden_weights;
	//Ϊ�������ݽṹ�����ڴ�ռ�
	input_unites=(double *) malloc ((unsigned) ((n_in+1) * sizeof (double)));
	hidden_unites=(double *) malloc ((unsigned) ((n_hidden+1) * sizeof (double)));
	output_unites=(double *) malloc ((unsigned) ((n_out+1) * sizeof (double)));
/*	input_weights = (double **) malloc ((unsigned) ((n_in+1) * sizeof (double *)));
	for (i = 0; i < n_in+1; i++)
	{
		input_weights[i] = (double *) malloc ((unsigned) ((n_hidden+1) * sizeof (double)));
	}
	hidden_weights = (double **) malloc ((unsigned) ((n_hidden+1) * sizeof (double *)));
	for (i = 0; i < n_hidden+1; i++)
	{
		hidden_weights[i] = (double *) malloc ((unsigned) ((n_out+1) * sizeof (double)));
	}
	
	
	//��ȡȨֵ
	CString path,path1;
	path=GetModuleDirectory();
	path1=path+"\\win2.dat";
	int pathlen = path1.GetLength(); 
	LPTSTR p = path1.GetBuffer(pathlen);
	//��ȡȨֵ
	if( r_weight(input_weights,n_in,n_hidden,p)==false)
		return 10;
	path1=path+"\\whi2.dat";
	pathlen = path1.GetLength(); 
	p = path1.GetBuffer(pathlen);
	if(r_weight(hidden_weights,n_hidden,n_out,p)==false)
		return 10;*/
	/*if( r_weight(input_weights,n_in,n_hidden,"E:\\jing\\��\\Recognitionp1\\win3.dat")==false)
		return;
	if(r_weight(hidden_weights,n_hidden,n_out,"E:\\jing\\��\\Recognitionp1\\whi3.dat")==false)
		return;*/
	
	
	//����ȡ�������������������͵��������
	for(i=1;i<=n_in;i++)
		input_unites[i]=data_in[i-1];
	int result=0;
	if(data_in[81]<0.5)
		result=30;
//	if(fabs(data_in[83]-data_in[82])>1)
//		result=30;
	if(result==0)
	{
		
		//ǰ�����뼤��
        bpnn_layerforward(input_unites,hidden_unites,
			input_weights_h, n_in,n_hidden);
        bpnn_layerforward(hidden_unites, output_unites,
			hidden_weights_h,n_hidden,n_out);
		
		//��������������ʶ��
		double a,b ;
		a=b=0;
		
		//����ÿһλ�����
		for(i=1;i<=n_out;i++)
		{
			//�������0.5��Ϊ1
			if(output_unites[i]>a)
			{
				a=output_unites[i];
				j=i;
			}
		}
		for(i=1;i<=n_out;i++)
		{
			if(i!=j)
				if(output_unites[i]>b)
					b=output_unites[i];
		}
		if((a-b>0.3&&a>0.5)||(a>0.7&&a-b>0.1))
			result=j-1;
		else
			result=20;
	}
	//����ж��Ľ��С�ڵ���9����Ϊ����
	if(result==1)
	{
		if(data_in[80]>1.4)
			result=7;
	}
	if(result<=9)
		recognize=result;
	//����ж��Ľ������9����Ϊ�����������λΪһ������ֵ20
	if(result==20)
		recognize=10;
	if(result==30)
		recognize=10;
	
	free(input_unites);
	free(hidden_unites);
	free(output_unites);
/*	for (i = 0; i < n_in+1; i++)
	{
		free(input_weights[i]); 
	}
	
	for (i = 0; i < n_hidden+1; i++)
	{
		free(hidden_weights[i]);
	}
	free(input_weights);
	free(hidden_weights);*/
	return recognize;
}


