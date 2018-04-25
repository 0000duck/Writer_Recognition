#include "processing.h"
#include "CString.h"

#ifndef _ADD_PROCESS_
#define _ADD_PROCESS_
#ifdef __cplusplus
extern "C" {
#endif


#define POSTCODE_LEN  11
typedef CRect  RECT ;
//typedef unsigned long LONG;
//typedef  char* CString;
typedef unsigned long DWORD;





//----�ַ��ṹ��-------------------------//
struct CPoint{
	int x;
	int y;
};
//---begin---��ͨԪ�㷨�ṹ��---begin----//
typedef struct a{
	int y;//�γ̵�Y����ֵ
	int beginx;//�γ̿�ʼ����X����ֵ
	int endx;//�γ�ĩ�˵�X����ֵ
	struct a *next;//ָ����ͨԪ�е���һ���γ�
	struct a *linenext;//ָ������е���һ���γ�
}runnode;//�γ̽ڵ�
typedef struct b{
	int minx;//��ͨԪ����������ص�����
	int maxx;//��ͨԪ�����ұ����ص�����
	int miny;//��ͨԪ�����ϱ����ص�����
	int maxy;//��ͨԪ�����±����ص�����
}shape;
typedef struct d{
	runnode *firstrunnode;//��ͨԪ�еĵ�һ���γ�
	runnode *lastrunnode;//ָ����ͨԪ�е����һ���γ�
	int value;//��ͨԪ�ı�ǩֵ
	shape compshape;//��ͨԪ����״��Ϣ
	int pixelnum;//��ͨԪ�е�ǰ��������
	BOOL sign;//��signΪTRUE�����ʾvalueֵ��ʾ��component������ͨԪ�ĸ�������ֵ����signΪFALSE,���ʾ��component������ͨԪ�ĸ�����valueֵ��ʾ����ɫֵ
	CPoint Barycenter;//��ͨԪ������
}component;

typedef struct e{
	int value;//component�ı�ǩֵ
	shape compshape;//component����������Ϣ
	int pixelnum;//component��ǰ��������
	BOOL sign;//��signΪTRUE�����ʾvalueֵ��ʾ��component������ͨԪ�ĸ�������ֵ����signΪFALSE,���ʾ��component������ͨԪ�ĸ�����valueֵ��ʾ����ɫֵ
	unsigned int 
		distance;//��ˮ�صķ�����Ϊ1����ˮ�ط������ϣ���Ϊ-1����ˮ�ط������£���Ϊ0����һ���ն�����Ϊ����ֵ����˵������һ����ˮ�أ��˲���תΪ���ֶ��壬�ܶ������û�����壩
	CPoint Barycenter;//component������
}CodeComponent;
//---end---��ͨԪ�㷨�ṹ��----end------//

#define Num_line  57  //�������
#define Num_cha   60  //ÿ�������ͨԪ����
typedef struct aa{
	RECT rect_a;
	int width;
	int height;
	int cenx;
	double wtoh;
}character; //�ַ��ṹ
typedef struct bb{
	RECT rect_c;
	int cenx;
	int  type_c; //�ַ����ͣ�0Ϊ���֣�1Ϊ���ֻ���ĸ��2Ϊ���ֲ�����3Ϊ�ֻ�
}character1; //�ַ�����
typedef struct cc{
	int left;
	int right;
}runnode1;
//-------end---�ַ��ṹ��--------------------//


//��������
/*ͼ������ȡ�ֻ�����-��APP���ú���int PhoneNum_Extraction()
*����ֵ���壺<��ӡˢ���ı���=0�����������ֻ����룻11��ȡ���ֻ�����
*�������壺proImage�Ҷ�ͼ�����ݣ�lWidthͼ���ȣ�lHeightͼ��߶ȣ�phonenum�ֻ�������ȡ���
*/

int PhoneNum_Extraction(int* proImage, int& lWidth,int& lHeight, char* cphone,int flag);//;CString &phonenum);



//�����ڲ����ú���
void initial();
int  Otsu(int * proImage, int lWidth, int lHeight, int stax, int stay,
				 int dx, int dy);
BOOL  ThresholdOtsu(int * proImage, int lWidth, int lHeight,int backcolor);
BOOL  ThresholdOtsu_add(int * proImage, int lWidth, int lHeight,int backcolor, RECT barRect);

//��������
int Partition(double * aaa, int low,int high,int * sn);
void Quick_Order(double * aaa,int left,int right,int * sn);

//�ֻ��зָ�
void post_segment(int* image,int width,character *chara_ori,character1 *chara_new1,int &num_c);
//�����ͨԪ
int ConCompLabelling8(int *lpDIB,int	lWidth,int	lHeight,CodeComponent *rescomponent,BOOL imageflag);
void ReleaseList(runnode **HeadRun,int Length);//�ͷ���ͨԪ�б��ڴ�
//�ϲ���ͨԪΪ����
void Comp_in(int * image, int lWidth, int lHeight, CodeComponent *rescomponent, int& digi);
//�Թ�����ͨԪ���½��д���
void reprocess(int *image, int lWidth, int lHeight, CodeComponent *rescomponent, int& digi);
//��ͨԪ����������
void Comp_Order(CodeComponent *rescomponent, int Comp_num);
//�зָ�
int row_segment(int *image, int lWidth, int lHeight, CodeComponent *rescomponent, int& digi, CodeComponent row_rescomponent[Num_line][Num_cha],int *row_x);
//�����зָ�
void row_segment1(CodeComponent *rescomponent, int Comp_num,CodeComponent row_rescomponent[Num_line][Num_cha],int &row_y, int *row_x,int num_r);
//�ϲ����ֲ���
void parts_merge(CodeComponent row_rescomponent[Num_line][Num_cha],int *row_x,int row_y);
//ʶ���ַ��
int Address_Rec(int* proImage, int lWidth,int lHeight, CodeComponent row_rescomponent[Num_line][Num_cha],int *row_x,int row_y, CString *rec_str);

//�ָ�ճ������
void segment_num(int* image,int lWidth,character1* chara_new, int num_index, int &num_c,int AB);
//HWʶ��
void OnRecognitionHw_lv(int* image, int width, int height);
//�����б�
int FontRecognition(CodeComponent *rescomponent, int digi);


//�����б�(����)
int FontRecognition_Cluster(CodeComponent *rescomponent, int digi);

//��Сdpi>12�ĵ�ַ��ͼ��
void Zoom_IAddress(int* image1,int* image2, int &width, int &height, float scale);
//ɾ���ǵ�ַ��
void row_recognition(int lWidth,int lHeight, CodeComponent row_rescomponent[Num_line][Num_cha],int *row_x,int row_y);
//������������ͨԪ,�����Ƿ��ǵ绰
bool Comp_Post(CodeComponent row_rescomponent[Num_line][Num_cha], int i, int j, int *row_x, int row_y);
//����
char vote_win(char* rec, int len);
//ͳ�Ƶ�ַ����ͨԪ�Ŀ��
void WidHei_Comp(CodeComponent row_rescomponent[Num_line][Num_cha],int *row_x,int row_y,int &temp_wid,int &temp_hei);
//������Ԥ��ȡ
void PreSegment_Line(int* proImage, int showLen, int showWid, int& iTop, int& iBottom);
bool Comp_Phone(character1* chara, int j);


#ifdef __cplusplus
}
#endif
#endif





