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





//----字符结构体-------------------------//
struct CPoint{
	int x;
	int y;
};
//---begin---连通元算法结构体---begin----//
typedef struct a{
	int y;//游程的Y座标值
	int beginx;//游程开始处的X座标值
	int endx;//游程末端的X座标值
	struct a *next;//指向连通元中的下一个游程
	struct a *linenext;//指向该行中的下一个游程
}runnode;//游程节点
typedef struct b{
	int minx;//连通元的最左边像素的坐标
	int maxx;//连通元的最右边像素的坐标
	int miny;//连通元的最上边像素的坐标
	int maxy;//连通元的最下边像素的坐标
}shape;
typedef struct d{
	runnode *firstrunnode;//连通元中的第一个游程
	runnode *lastrunnode;//指向连通元中的最后一个游程
	int value;//连通元的标签值
	shape compshape;//连通元的形状信息
	int pixelnum;//连通元中的前景像素数
	BOOL sign;//若sign为TRUE，则表示value值表示该component所在连通元的根的索引值，若sign为FALSE,则表示该component所在连通元的根，其value值表示其颜色值
	CPoint Barycenter;//连通元的重心
}component;

typedef struct e{
	int value;//component的标签值
	shape compshape;//component的外界矩阵信息
	int pixelnum;//component的前景像素数
	BOOL sign;//若sign为TRUE，则表示value值表示该component所在连通元的根的索引值，若sign为FALSE,则表示该component所在连通元的根，其value值表示其颜色值
	unsigned int 
		distance;//蓄水池的方向，若为1则蓄水池方向向上，若为-1则蓄水池方向向下，若为0则是一个空洞，若为其他值，则说明不是一个蓄水池（此参数转为数字定义，很多情况下没有意义）
	CPoint Barycenter;//component的重心
}CodeComponent;
//---end---连通元算法结构体----end------//

#define Num_line  57  //最大行数
#define Num_cha   60  //每行最大连通元个数
typedef struct aa{
	RECT rect_a;
	int width;
	int height;
	int cenx;
	double wtoh;
}character; //字符结构
typedef struct bb{
	RECT rect_c;
	int cenx;
	int  type_c; //字符类型，0为汉字，1为数字或字母，2为汉字部件，3为手机
}character1; //字符类型
typedef struct cc{
	int left;
	int right;
}runnode1;
//-------end---字符结构体--------------------//


//函数声明
/*图像中提取手机号码-供APP调用函数int PhoneNum_Extraction()
*返回值定义：<非印刷体文本；=0正常返回无手机号码；11提取到手机号码
*参数定义：proImage灰度图像数据，lWidth图像宽度，lHeight图像高度，phonenum手机号码提取结果
*/

int PhoneNum_Extraction(int* proImage, int& lWidth,int& lHeight, char* cphone,int flag);//;CString &phonenum);



//以下内部调用函数
void initial();
int  Otsu(int * proImage, int lWidth, int lHeight, int stax, int stay,
				 int dx, int dy);
BOOL  ThresholdOtsu(int * proImage, int lWidth, int lHeight,int backcolor);
BOOL  ThresholdOtsu_add(int * proImage, int lWidth, int lHeight,int backcolor, RECT barRect);

//快速排序
int Partition(double * aaa, int low,int high,int * sn);
void Quick_Order(double * aaa,int left,int right,int * sn);

//手机行分割
void post_segment(int* image,int width,character *chara_ori,character1 *chara_new1,int &num_c);
//标记连通元
int ConCompLabelling8(int *lpDIB,int	lWidth,int	lHeight,CodeComponent *rescomponent,BOOL imageflag);
void ReleaseList(runnode **HeadRun,int Length);//释放连通元列表内存
//合并连通元为汉字
void Comp_in(int * image, int lWidth, int lHeight, CodeComponent *rescomponent, int& digi);
//对过大连通元重新进行处理
void reprocess(int *image, int lWidth, int lHeight, CodeComponent *rescomponent, int& digi);
//连通元从左到右排序
void Comp_Order(CodeComponent *rescomponent, int Comp_num);
//行分割
int row_segment(int *image, int lWidth, int lHeight, CodeComponent *rescomponent, int& digi, CodeComponent row_rescomponent[Num_line][Num_cha],int *row_x);
//重新行分割
void row_segment1(CodeComponent *rescomponent, int Comp_num,CodeComponent row_rescomponent[Num_line][Num_cha],int &row_y, int *row_x,int num_r);
//合并汉字部件
void parts_merge(CodeComponent row_rescomponent[Num_line][Num_cha],int *row_x,int row_y);
//识别地址块
int Address_Rec(int* proImage, int lWidth,int lHeight, CodeComponent row_rescomponent[Num_line][Num_cha],int *row_x,int row_y, CString *rec_str);

//分割粘连数字
void segment_num(int* image,int lWidth,character1* chara_new, int num_index, int &num_c,int AB);
//HW识别
void OnRecognitionHw_lv(int* image, int width, int height);
//字体判别
int FontRecognition(CodeComponent *rescomponent, int digi);


//字体判别(聚类)
int FontRecognition_Cluster(CodeComponent *rescomponent, int digi);

//缩小dpi>12的地址块图像
void Zoom_IAddress(int* image1,int* image2, int &width, int &height, float scale);
//删除非地址行
void row_recognition(int lWidth,int lHeight, CodeComponent row_rescomponent[Num_line][Num_cha],int *row_x,int row_y);
//分析连续的连通元,判其是否是电话
bool Comp_Post(CodeComponent row_rescomponent[Num_line][Num_cha], int i, int j, int *row_x, int row_y);
//决策
char vote_win(char* rec, int len);
//统计地址行连通元的宽高
void WidHei_Comp(CodeComponent row_rescomponent[Num_line][Num_cha],int *row_x,int row_y,int &temp_wid,int &temp_hei);
//号码行预提取
void PreSegment_Line(int* proImage, int showLen, int showWid, int& iTop, int& iBottom);
bool Comp_Phone(character1* chara, int j);


#ifdef __cplusplus
}
#endif
#endif





