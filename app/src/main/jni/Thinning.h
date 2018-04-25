#ifndef _INC_ThinningAPI
#define _INC_ThinningAPI
#include "string.h"
#include "vector"
#include "string.h"
// 函数声明
#define BYTE int
void ThinRosenfeld(int *image, int lx, int ly);
void expanding(int* image,int lWidth, int lHeight);

std::vector<double>  GetMFeature1(int* lpDIBBits, int lWidth, int lHeight, int flag); //应该是标准的cdf flag = 1 则是标准结果 遇到点对 跳出循环  =0则不跳出

//xyj
#endif //!_INC_MorphAPI
