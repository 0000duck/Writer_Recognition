#ifndef _INC_ThinningAPI
#define _INC_ThinningAPI
#include "string.h"
#include "vector"
#include "string.h"
// ��������
#define BYTE int
void ThinRosenfeld(int *image, int lx, int ly);
void expanding(int* image,int lWidth, int lHeight);

std::vector<double>  GetMFeature1(int* lpDIBBits, int lWidth, int lHeight, int flag); //Ӧ���Ǳ�׼��cdf flag = 1 ���Ǳ�׼��� ������� ����ѭ��  =0������

//xyj
#endif //!_INC_MorphAPI
