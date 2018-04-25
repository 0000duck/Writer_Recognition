#ifndef _RSECNUM
#define _RSECNUM


void jing_ini_np(const char *num,const char *win2,const char*whi2,const char*model);//印刷体数字识别初始化
int recnum_jing(int* ImageBuffer,int width,int height);
int recnum_jingsvm(int* ImageBuffer,int width,int height);
void jing_ini_nh(char *num_h,char *win_h,char*whi_h );//手写体数字识别初始化
int recnum_jing_h(int* ImageBuffer,int width,int height);





#endif
