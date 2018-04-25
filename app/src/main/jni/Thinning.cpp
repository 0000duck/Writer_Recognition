#include "Thinning.h"
//#include "DIBAPI.h"
#include <math.h>
#include<stdlib.h>


/////////////////////////////////////////////////////////////////////////
//Rosenfeld细化算法
//功能：对图象进行细化
//参数：image：代表图象的一维数组
//      lx：图象宽度
//      ly：图象高度
//      无返回值
void ThinRosenfeld(int *image, int lx, int ly)
{
	int *f, *g;
	int n[10];
	int  a[5] = {0, -1, 1, 0, 0};
	int b[5] = {0, 0, 0, 1, -1};
	int  nrnd, cond, n48, n26, n24, n46, n68, n82, n123, n345, n567, n781;
	int k, shori;
	int i, j;
	int ii, jj, kk, kk1, kk2, kk3;

	g = (int  *)malloc(lx * ly*sizeof(int));
	if(g==NULL)
	{
		//printf("error in alocating mmeory!\n");
		return;
	}

	f = image;
	memcpy(g,f,lx * ly*sizeof(int));


	do
	{
		shori = 0;
		for(k=1; k<=4; k++)
		{
			for(i=1; i<lx-1; i++)
			{
				ii = i + a[k];

				for(j=1; j<ly-1; j++)
				{
					kk = i*ly + j;

					if(!f[kk])
						continue;

					jj = j + b[k];
					kk1 = ii*ly + jj;

					if(f[kk1])
						continue;

					kk1 = kk - ly -1;
					kk2 = kk1 + 1;
					kk3 = kk2 + 1;
					n[3] = f[kk1];
					n[2] = f[kk2];
					n[1] = f[kk3];
					kk1 = kk - 1;
					kk3 = kk + 1;
					n[4] = f[kk1];
					n[8] = f[kk3];
					kk1 = kk + ly - 1;
					kk2 = kk1 + 1;
					kk3 = kk2 + 1;
					n[5] = f[kk1];
					n[6] = f[kk2];
					n[7] = f[kk3];

					nrnd = n[1] + n[2] + n[3] + n[4]
						+n[5] + n[6] + n[7] + n[8];
					if(nrnd<=1)
						continue;

					cond = 0;
					n48 = n[4] + n[8];
					n26 = n[2] + n[6];
					n24 = n[2] + n[4];
					n46 = n[4] + n[6];
					n68 = n[6] + n[8];
					n82 = n[8] + n[2];
					n123 = n[1] + n[2] + n[3];
					n345 = n[3] + n[4] + n[5];
					n567 = n[5] + n[6] + n[7];
					n781 = n[7] + n[8] + n[1];

					if(n[2]==1 && n48==0 && n567>0)
					{
						if(!cond)
							continue;
						g[kk] = 0;
						shori = 1;
						continue;
					}

					if(n[6]==1 && n48==0 && n123>0)
					{
						if(!cond)
							continue;
						g[kk] = 0;
						shori = 1;
						continue;
					}

					if(n[8]==1 && n26==0 && n345>0)
					{
						if(!cond)
							continue;
						g[kk] = 0;
						shori = 1;
						continue;
					}

					if(n[4]==1 && n26==0 && n781>0)
					{
						if(!cond)
							continue;
						g[kk] = 0;
						shori = 1;
						continue;
					}

					if(n[5]==1 && n46==0)
					{
						if(!cond)
							continue;
						g[kk] = 0;
						shori = 1;
						continue;
					}

					if(n[7]==1 && n68==0)
					{
						if(!cond)
							continue;
						g[kk] = 0;
						shori = 1;
						continue;
					}

					if(n[1]==1 && n82==0)
					{
						if(!cond)
							continue;
						g[kk] = 0;
						shori = 1;
						continue;
					}

					if(n[3]==1 && n24==0)
					{
						if(!cond)
							continue;
						g[kk] = 0;
						shori = 1;
						continue;
					}

					cond = 1;
					if(!cond)
						continue;
					g[kk] = 0;
					shori = 1;
				}
			}

			for(i=0; i<lx; i++)
			{
				for(j=0; j<ly; j++)
				{
					kk = i*ly + j;
					f[kk] = g[kk];
				}
			}
		}
	}while(shori);

	free(g);
}					


void expanding(int* image,int lWidth, int lHeight)
{
	int i,j,m,n;
	int pixel;
	for(i=0;i<lHeight;i++)
	{
		for(j=0;j<lWidth;j++)
		{
			pixel=image[lWidth*i+j];
			if(pixel==0)
			{
				for(m=0;m<3;m++)
				{
					for(n=0;n<3;n++)
					{
						pixel=image[lWidth*(i+m-1)+(j+n-1)];
						if(pixel==255)
							image[lWidth*(i+m-1)+(j+n-1)]=1;
					}
				}
			}
		}
	}
	for(i=0;i<lHeight;i++)
	{
		for(j=0;j<lWidth;j++)
		{
			pixel=image[lWidth*i+j];
			if(pixel==1)
				image[lWidth*i+j]=0;
		}
	}
				
}

std::vector<double> GetMFeature1(BYTE* lpDIBBits, int lWidth, int lHeight, int flag) //应该是标准的cdf flag = 1 则是标准结果 遇到点对 跳出循环  =0则不跳出
{
	char *	lpSrc;				// 指向源图像的指针
	char *	lpDst;				// 指向缓存图像的指针
	char *	lpNewDIBBits;		// 指向缓存DIB图像的指针

	long i,j;					//循环变量
	unsigned char mfeature1[8];
	unsigned char mfeature2[16];
	unsigned char mfeature3[24];
	unsigned char mfeature4[32];
	unsigned char mfeature5[40];
	unsigned char mfeature6[48];
	unsigned char mfeature7[56];
	unsigned char pixel;		//像素值
	double  feature1[28]={0};
	double feature2[120]={0};//记录5*5
	double feature3[276]={0};//记录7*7
	double feature4[496]={0};//记录9*9
	double feature5[780]={0};//记录11*11
	double feature6[1128]={0};//记录13*13
	double feature7[1540]={0};//记录15*15
	double feature8[2016]={0};//记录17*17


	std::vector<double> probability;
	unsigned long sum=0;
	// 暂时分配内存，以保存新图像


	// 初始化新分配的内存，设定初始值为255
	lpDst = (char *)lpNewDIBBits;
	memset(lpDst, (BYTE)255, lWidth * lHeight);
	for(j = 7; j <lHeight-7; j++)
	{
		for(i = 7;i <lWidth-7; i++)
		{
			// 指向源图像倒数第j行，第i个象素的指针
			lpSrc = (char *)lpDIBBits + lWidth * j + i;
			// 指向目标图像倒数第j行，第i个象素的指针
			lpDst = (char *)lpNewDIBBits + lWidth * j + i;
			//取得当前指针处的像素值，注意要转换为unsigned char型
			pixel = (unsigned char)*lpSrc;

			if(pixel == 0)
			{
				*lpDst = (unsigned char)0;
				//记录3*3周围像素情况
				mfeature1[0]=(unsigned char)*(lpSrc +1);
				mfeature1[1]=(unsigned char)*(lpSrc - lWidth +1);
				mfeature1[2]=(unsigned char)*(lpSrc - lWidth );
				mfeature1[3]=(unsigned char)*(lpSrc - lWidth -1);
				mfeature1[4]=(unsigned char)*(lpSrc -1);
				mfeature1[5]=(unsigned char)*(lpSrc + lWidth -1);
				mfeature1[6]=(unsigned char)*(lpSrc + lWidth );
				mfeature1[7]=(unsigned char)*(lpSrc + lWidth +1);
				//记录5*5周围像素情况
				mfeature2[0]=(unsigned char)*(lpSrc +2);
				mfeature2[1]=(unsigned char)*(lpSrc - lWidth +2);
				mfeature2[2]=(unsigned char)*(lpSrc - 2*lWidth+2);
				mfeature2[3]=(unsigned char)*(lpSrc - 2*lWidth +1);
				mfeature2[4]=(unsigned char)*(lpSrc -2*lWidth);
				mfeature2[5]=(unsigned char)*(lpSrc -2*lWidth -1);
				mfeature2[6]=(unsigned char)*(lpSrc -2*lWidth -2 );
				mfeature2[7]=(unsigned char)*(lpSrc - lWidth -2);
				mfeature2[8]=(unsigned char)*(lpSrc -2);
				mfeature2[9]=(unsigned char)*(lpSrc + lWidth-2);
				mfeature2[10]=(unsigned char)*(lpSrc + 2*lWidth-2 );
				mfeature2[11]=(unsigned char)*(lpSrc + 2*lWidth-1);
				mfeature2[12]=(unsigned char)*(lpSrc + 2*lWidth);
				mfeature2[13]=(unsigned char)*(lpSrc +  2*lWidth +1);
				mfeature2[14]=(unsigned char)*(lpSrc +  2*lWidth +2 );
				mfeature2[15]=(unsigned char)*(lpSrc + lWidth +2);
				//记录7*7周围像素情况
				mfeature3[0]=(unsigned char)*(lpSrc +3);
				mfeature3[1]=(unsigned char)*(lpSrc - lWidth +3);
				mfeature3[2]=(unsigned char)*(lpSrc - 2*lWidth +3);
				mfeature3[3]=(unsigned char)*(lpSrc - 3*lWidth +3);
				mfeature3[4]=(unsigned char)*(lpSrc - 3*lWidth +2);
				mfeature3[5]=(unsigned char)*(lpSrc - 3*lWidth +1);
				mfeature3[6]=(unsigned char)*(lpSrc - 3*lWidth );
				mfeature3[7]=(unsigned char)*(lpSrc - 3*lWidth-1);
				mfeature3[8]=(unsigned char)*(lpSrc - 3*lWidth-2);
				mfeature3[9]=(unsigned char)*(lpSrc - 3*lWidth-3);
				mfeature3[10]=(unsigned char)*(lpSrc - 2*lWidth-3 );
				mfeature3[11]=(unsigned char)*(lpSrc - lWidth-3);
				mfeature3[12]=(unsigned char)*(lpSrc -3);
				mfeature3[13]=(unsigned char)*(lpSrc + lWidth -3);
				mfeature3[14]=(unsigned char)*(lpSrc + 2*lWidth -3 );
				mfeature3[15]=(unsigned char)*(lpSrc + 3*lWidth -3);
				mfeature3[16]=(unsigned char)*(lpSrc + 3*lWidth -2);
				mfeature3[17]=(unsigned char)*(lpSrc + 3*lWidth -1);
				mfeature3[18]=(unsigned char)*(lpSrc + 3*lWidth );
				mfeature3[19]=(unsigned char)*(lpSrc + 3*lWidth +1);
				mfeature3[20]=(unsigned char)*(lpSrc + 3*lWidth +2);
				mfeature3[21]=(unsigned char)*(lpSrc + 3*lWidth +3);
				mfeature3[22]=(unsigned char)*(lpSrc + 2*lWidth +3 );
				mfeature3[23]=(unsigned char)*(lpSrc + lWidth +3);
				//记录9*9周围像素情况
				mfeature4[0]=(unsigned char)*(lpSrc +4);
				mfeature4[1]=(unsigned char)*(lpSrc - lWidth +4);
				mfeature4[2]=(unsigned char)*(lpSrc - 2*lWidth +4);
				mfeature4[3]=(unsigned char)*(lpSrc - 3*lWidth +4);
				mfeature4[4]=(unsigned char)*(lpSrc - 4*lWidth +4);
				mfeature4[5]=(unsigned char)*(lpSrc - 4*lWidth +3);
				mfeature4[6]=(unsigned char)*(lpSrc - 4*lWidth +2 );
				mfeature4[7]=(unsigned char)*(lpSrc - 4*lWidth+1);
				mfeature4[8]=(unsigned char)*(lpSrc - 4*lWidth);
				mfeature4[9]=(unsigned char)*(lpSrc - 4*lWidth-1);
				mfeature4[10]=(unsigned char)*(lpSrc - 4*lWidth-2 );
				mfeature4[11]=(unsigned char)*(lpSrc - 4*lWidth-3);
				mfeature4[12]=(unsigned char)*(lpSrc - 4*lWidth-4);
				mfeature4[13]=(unsigned char)*(lpSrc - 3*lWidth -4);
				mfeature4[14]=(unsigned char)*(lpSrc - 2*lWidth -4 );
				mfeature4[15]=(unsigned char)*(lpSrc - lWidth -4);
				mfeature4[16]=(unsigned char)*(lpSrc -4);
				mfeature4[17]=(unsigned char)*(lpSrc + lWidth -4);
				mfeature4[18]=(unsigned char)*(lpSrc + 2*lWidth -4);
				mfeature4[19]=(unsigned char)*(lpSrc + 3*lWidth -4 );
				mfeature4[20]=(unsigned char)*(lpSrc + 4*lWidth -4);
				mfeature4[21]=(unsigned char)*(lpSrc + 4*lWidth -3);
				mfeature4[22]=(unsigned char)*(lpSrc + 4*lWidth -2);
				mfeature4[23]=(unsigned char)*(lpSrc + 4*lWidth -1 );
				mfeature4[24]=(unsigned char)*(lpSrc + 4*lWidth );
				mfeature4[25]=(unsigned char)*(lpSrc + 4*lWidth +1);
				mfeature4[26]=(unsigned char)*(lpSrc + 4*lWidth +2);
				mfeature4[27]=(unsigned char)*(lpSrc + 4*lWidth +3);
				mfeature4[28]=(unsigned char)*(lpSrc + 4*lWidth +4);
				mfeature4[29]=(unsigned char)*(lpSrc + 3*lWidth +4);
				mfeature4[30]=(unsigned char)*(lpSrc + 2*lWidth +4);
				mfeature4[31]=(unsigned char)*(lpSrc + lWidth +4);
				//记录11*11周围像素情况
				mfeature5[0]=(unsigned char)*(lpSrc +5);
				mfeature5[1]=(unsigned char)*(lpSrc - lWidth +5);
				mfeature5[2]=(unsigned char)*(lpSrc - 2*lWidth +5);
				mfeature5[3]=(unsigned char)*(lpSrc - 3*lWidth +5);
				mfeature5[4]=(unsigned char)*(lpSrc - 4*lWidth +5);
				mfeature5[5]=(unsigned char)*(lpSrc - 5*lWidth +5);
				mfeature5[6]=(unsigned char)*(lpSrc - 5*lWidth +4);
				mfeature5[7]=(unsigned char)*(lpSrc - 5*lWidth +3);
				mfeature5[8]=(unsigned char)*(lpSrc - 5*lWidth +2);
				mfeature5[9]=(unsigned char)*(lpSrc - 5*lWidth +1);
				mfeature5[10]=(unsigned char)*(lpSrc - 5*lWidth );
				mfeature5[11]=(unsigned char)*(lpSrc - 5*lWidth-1);
				mfeature5[12]=(unsigned char)*(lpSrc - 5*lWidth-2);
				mfeature5[13]=(unsigned char)*(lpSrc - 5*lWidth-3);
				mfeature5[14]=(unsigned char)*(lpSrc - 5*lWidth -4);
				mfeature5[15]=(unsigned char)*(lpSrc - 5*lWidth -5);
				mfeature5[16]=(unsigned char)*(lpSrc - 4*lWidth -5);
				mfeature5[17]=(unsigned char)*(lpSrc - 3*lWidth -5);
				mfeature5[18]=(unsigned char)*(lpSrc - 2*lWidth -5 );
				mfeature5[19]=(unsigned char)*(lpSrc - lWidth -5);
				mfeature5[20]=(unsigned char)*(lpSrc -5);
				mfeature5[21]=(unsigned char)*(lpSrc + lWidth -5);
				mfeature5[22]=(unsigned char)*(lpSrc + 2*lWidth -5 );
				mfeature5[23]=(unsigned char)*(lpSrc + 3*lWidth -5);
				mfeature5[24]=(unsigned char)*(lpSrc + 4*lWidth -5);
				mfeature5[25]=(unsigned char)*(lpSrc + 5*lWidth -5);
				mfeature5[26]=(unsigned char)*(lpSrc + 5*lWidth -4);
				mfeature5[27]=(unsigned char)*(lpSrc + 5*lWidth -3);
				mfeature5[28]=(unsigned char)*(lpSrc + 5*lWidth -2);
				mfeature5[29]=(unsigned char)*(lpSrc + 5*lWidth -1);
				mfeature5[30]=(unsigned char)*(lpSrc + 5*lWidth);
				mfeature5[31]=(unsigned char)*(lpSrc + 5*lWidth+1);
				mfeature5[32]=(unsigned char)*(lpSrc + 5*lWidth+2);
				mfeature5[33]=(unsigned char)*(lpSrc + 5*lWidth+3 );
				mfeature5[34]=(unsigned char)*(lpSrc + 5*lWidth+4);
				mfeature5[35]=(unsigned char)*(lpSrc + 5*lWidth+5);
				mfeature5[36]=(unsigned char)*(lpSrc + 4*lWidth+5 );
				mfeature5[37]=(unsigned char)*(lpSrc + 3*lWidth +5 );
				mfeature5[38]=(unsigned char)*(lpSrc + 2*lWidth +5);
				mfeature5[39]=(unsigned char)*(lpSrc + lWidth +5);
				//记录13*13周围像素情况
				mfeature6[0]=(unsigned char)*(lpSrc +6);
				mfeature6[1]=(unsigned char)*(lpSrc - lWidth +6);
				mfeature6[2]=(unsigned char)*(lpSrc - 2*lWidth +6);
				mfeature6[3]=(unsigned char)*(lpSrc - 3*lWidth +6);
				mfeature6[4]=(unsigned char)*(lpSrc - 4*lWidth +6);
				mfeature6[5]=(unsigned char)*(lpSrc - 5*lWidth +6);
				mfeature6[6]=(unsigned char)*(lpSrc - 6*lWidth +6);
				mfeature6[7]=(unsigned char)*(lpSrc - 6*lWidth +5);
				mfeature6[8]=(unsigned char)*(lpSrc - 6*lWidth +4);
				mfeature6[9]=(unsigned char)*(lpSrc - 6*lWidth +3);
				mfeature6[10]=(unsigned char)*(lpSrc - 6*lWidth +2);
				mfeature6[11]=(unsigned char)*(lpSrc - 6*lWidth +1);
				mfeature6[12]=(unsigned char)*(lpSrc - 6*lWidth  );
				mfeature6[13]=(unsigned char)*(lpSrc - 6*lWidth -1);
				mfeature6[14]=(unsigned char)*(lpSrc - 6*lWidth -2);
				mfeature6[15]=(unsigned char)*(lpSrc - 6*lWidth -3);
				mfeature6[16]=(unsigned char)*(lpSrc - 6*lWidth -4);
				mfeature6[17]=(unsigned char)*(lpSrc - 6*lWidth -5);
				mfeature6[18]=(unsigned char)*(lpSrc - 6*lWidth -6);
				mfeature6[19]=(unsigned char)*(lpSrc - 5*lWidth -6);
				mfeature6[20]=(unsigned char)*(lpSrc - 4*lWidth -6);
				mfeature6[21]=(unsigned char)*(lpSrc - 3*lWidth -6);
				mfeature6[22]=(unsigned char)*(lpSrc - 2*lWidth -6);
				mfeature6[23]=(unsigned char)*(lpSrc - lWidth -6);
				mfeature6[24]=(unsigned char)*(lpSrc -6);
				mfeature6[25]=(unsigned char)*(lpSrc + lWidth -6);
				mfeature6[26]=(unsigned char)*(lpSrc + 2*lWidth -6);
				mfeature6[27]=(unsigned char)*(lpSrc + 3*lWidth -6);
				mfeature6[28]=(unsigned char)*(lpSrc + 4*lWidth -6);
				mfeature6[29]=(unsigned char)*(lpSrc + 5*lWidth -6);
				mfeature6[30]=(unsigned char)*(lpSrc + 6*lWidth -6);
				mfeature6[31]=(unsigned char)*(lpSrc + 6*lWidth -5);
				mfeature6[32]=(unsigned char)*(lpSrc + 6*lWidth -4);
				mfeature6[33]=(unsigned char)*(lpSrc + 6*lWidth -3);
				mfeature6[34]=(unsigned char)*(lpSrc + 6*lWidth -2);
				mfeature6[35]=(unsigned char)*(lpSrc + 6*lWidth -1);
				mfeature6[36]=(unsigned char)*(lpSrc + 6*lWidth );
				mfeature6[37]=(unsigned char)*(lpSrc + 6*lWidth +1);
				mfeature6[38]=(unsigned char)*(lpSrc + 6*lWidth +2);
				mfeature6[39]=(unsigned char)*(lpSrc + 6*lWidth +3);
				mfeature6[40]=(unsigned char)*(lpSrc + 6*lWidth +4);
				mfeature6[41]=(unsigned char)*(lpSrc + 6*lWidth +5);
				mfeature6[42]=(unsigned char)*(lpSrc + 6*lWidth +6);
				mfeature6[43]=(unsigned char)*(lpSrc + 5*lWidth +6);
				mfeature6[44]=(unsigned char)*(lpSrc + 4*lWidth +6);
				mfeature6[45]=(unsigned char)*(lpSrc + 3*lWidth +6);
				mfeature6[46]=(unsigned char)*(lpSrc + 2*lWidth +6);
				mfeature6[47]=(unsigned char)*(lpSrc + lWidth +6);
				//记录15*15周围像素情况
				mfeature7[0]=(unsigned char)*(lpSrc +7);
				mfeature7[1]=(unsigned char)*(lpSrc - lWidth +7);
				mfeature7[2]=(unsigned char)*(lpSrc - 2*lWidth +7);
				mfeature7[3]=(unsigned char)*(lpSrc - 3*lWidth +7);
				mfeature7[4]=(unsigned char)*(lpSrc - 4*lWidth +7);
				mfeature7[5]=(unsigned char)*(lpSrc - 5*lWidth +7);
				mfeature7[6]=(unsigned char)*(lpSrc - 6*lWidth +7);
				mfeature7[7]=(unsigned char)*(lpSrc - 7*lWidth +7);
				mfeature7[8]=(unsigned char)*(lpSrc - 7*lWidth +6);
				mfeature7[9]=(unsigned char)*(lpSrc - 7*lWidth +5);
				mfeature7[10]=(unsigned char)*(lpSrc - 7*lWidth +4);
				mfeature7[11]=(unsigned char)*(lpSrc - 7*lWidth +3);
				mfeature7[12]=(unsigned char)*(lpSrc - 7*lWidth +2 );
				mfeature7[13]=(unsigned char)*(lpSrc - 7*lWidth +1);
				mfeature7[14]=(unsigned char)*(lpSrc - 7*lWidth );
				mfeature7[15]=(unsigned char)*(lpSrc - 7*lWidth -1);
				mfeature7[16]=(unsigned char)*(lpSrc - 7*lWidth -2);
				mfeature7[17]=(unsigned char)*(lpSrc - 7*lWidth -3);
				mfeature7[18]=(unsigned char)*(lpSrc - 7*lWidth -4);
				mfeature7[19]=(unsigned char)*(lpSrc - 7*lWidth -5);
				mfeature7[20]=(unsigned char)*(lpSrc - 7*lWidth -6);
				mfeature7[21]=(unsigned char)*(lpSrc - 7*lWidth -7);
				mfeature7[22]=(unsigned char)*(lpSrc - 6*lWidth -7);
				mfeature7[23]=(unsigned char)*(lpSrc - 5*lWidth -7);
				mfeature7[24]=(unsigned char)*(lpSrc - 4*lWidth -7);
				mfeature7[25]=(unsigned char)*(lpSrc - 3*lWidth -7);
				mfeature7[26]=(unsigned char)*(lpSrc - 2*lWidth -7);
				mfeature7[27]=(unsigned char)*(lpSrc - lWidth -7);
				mfeature7[28]=(unsigned char)*(lpSrc -7);
				mfeature7[29]=(unsigned char)*(lpSrc + lWidth -7);
				mfeature7[30]=(unsigned char)*(lpSrc + 2*lWidth -7);
				mfeature7[31]=(unsigned char)*(lpSrc + 3*lWidth -7);
				mfeature7[32]=(unsigned char)*(lpSrc + 4*lWidth -7);
				mfeature7[33]=(unsigned char)*(lpSrc + 5*lWidth -7);
				mfeature7[34]=(unsigned char)*(lpSrc + 6*lWidth -7);
				mfeature7[35]=(unsigned char)*(lpSrc + 7*lWidth -7);
				mfeature7[36]=(unsigned char)*(lpSrc + 7*lWidth -6);
				mfeature7[37]=(unsigned char)*(lpSrc + 7*lWidth -5);
				mfeature7[38]=(unsigned char)*(lpSrc + 7*lWidth -4);
				mfeature7[39]=(unsigned char)*(lpSrc + 7*lWidth -3);
				mfeature7[40]=(unsigned char)*(lpSrc + 7*lWidth -2);
				mfeature7[41]=(unsigned char)*(lpSrc + 7*lWidth -1);
				mfeature7[42]=(unsigned char)*(lpSrc + 7*lWidth );
				mfeature7[43]=(unsigned char)*(lpSrc + 7*lWidth +1);
				mfeature7[44]=(unsigned char)*(lpSrc + 7*lWidth +2);
				mfeature7[45]=(unsigned char)*(lpSrc + 7*lWidth +3);
				mfeature7[46]=(unsigned char)*(lpSrc + 7*lWidth +4);
				mfeature7[47]=(unsigned char)*(lpSrc + 7*lWidth +5);
				mfeature7[48]=(unsigned char)*(lpSrc + 7*lWidth +6);
				mfeature7[49]=(unsigned char)*(lpSrc + 7*lWidth +7);
				mfeature7[50]=(unsigned char)*(lpSrc + 6*lWidth +7);
				mfeature7[51]=(unsigned char)*(lpSrc + 5*lWidth +7);
				mfeature7[52]=(unsigned char)*(lpSrc + 4*lWidth +7);
				mfeature7[53]=(unsigned char)*(lpSrc + 3*lWidth +7);
				mfeature7[54]=(unsigned char)*(lpSrc + 2*lWidth +7);
				mfeature7[55]=(unsigned char)*(lpSrc + 1*lWidth +7);
				int a,b,c,d;
				int flag2=0;
				int flag3=0;
				int flag41=0;
				int flag42=0;
				int flag5=0;
				int flag61=0;
				int flag62=0;
				int flag63=0;
				int flag7=0;
				int flag83=0;
				int flag81=0;
				int flag82=0;
/*****************
从水平开始以逆时针旋转搜索像素点的3*3领域的点，记录下是GMF的像素。
*****************/
				for(a=0;a<8;a++){
					if(mfeature1[a]==0){
						for(b=a+1;b<8;b++){
							if(mfeature1[b]==0){
								feature1[a*(15-a)/2+b-a-1]=feature1[a*(15-a)/2+b-a-1]+1;
								a=b;
								if(flag == 1)
								{
									break;
								}
							}
						}
					}
				}
/*****************
从水平开始以逆时针旋转搜索像素点的5*5领域的点，记录下是GMF的像素。
*****************/
				for(a=0;a<16;a++){
					if(mfeature2[a]==0){
						for(b=a+1;b<16;b++){
							if(mfeature2[b]==0){
								for(c=0;c<8;c++)
								{
									if (a==2*c){
										for(d=0;d<8;d++){
											if (b==2*d){
												feature1[c*(15-c)/2+d-c-1]=feature1[c*(15-c)/2+d-c-1]+1;
												a=b;
												flag2=1;
												if(flag == 1)
												{
													break;
												}
											}
										}
									}
								}
								if(flag2==0)
								{
									feature2[a*(31-a)/2+b-a-1]=feature2[a*(31-a)/2+b-a-1]+1;
									a=b;
									if(flag == 1) {	break;}
								}
								flag2=0;
							}
						}
					}
				}
/*****************
从水平开始以逆时针旋转搜索像素点的7*7领域的点，记录下是GMF的像素。
*****************/
				for(a=0;a<24;a++){
					if(mfeature3[a]==0){
						for(b=a+1;b<24;b++){
							if(mfeature3[b]==0){
								for(c=0;c<8;c++){
									if (a==3*c){
										for(d=0;d<8;d++){
											if (b==3*d){
												feature1[c*(15-c)/2+d-c-1]=feature1[c*(15-c)/2+d-c-1]+1;
												a=b;
												flag3=1;
												if(flag == 1) {break;}
											}
										}
									}
								}
								if(flag3==0){
									feature3[a*(47-a)/2+b-a-1]=feature3[a*(47-a)/2+b-a-1]+1;
									a=b;
									if(flag == 1) {break;}
								}
								flag3=0;
							}
						}
					}
				}
/*****************
从水平开始以逆时针旋转搜索像素点的9*9领域的点，记录下是GMF的像素。
*****************/
				for(a=0;a<32;a++){
					if(mfeature4[a]==0){
						for(b=a+1;b<32;b++){
							if(mfeature4[b]==0){
								for(c=0;c<8;c++){
									if (a==4*c){
										for(d=0;d<8;d++){
											if (b==4*d){
												feature1[c*(15-c)/2+d-c-1]=feature1[c*(15-c)/2+d-c-1]+1;
												a=b;
												flag41=1;
												if(flag == 1) {	break;}
											}
										}
									}
								}
								for(c=0;c<16;c++){
									if (a==2*c){
										for(d=0;d<16;d++){
											if (b==2*d){
												if(flag41==0){
													feature2[c*(31-c)/2+d-c-1]=feature2[c*(31-c)/2+d-c-1]+1;
													a=b;
													flag42=1;
													if(flag == 1) {	break;}
												}
											}
										}
									}
								}
								if(flag41==0&&flag42==0){
									feature4[a*(63-a)/2+b-a-1]=feature4[a*(63-a)/2+b-a-1]+1;
									a=b;
									if(flag == 1) {	break;}
								}
								flag41=0;
								flag42=0;
							}
						}
					}
				}
/*****************
从水平开始以逆时针旋转搜索像素点的11*11领域的点，记录下是GMF的像素。
*****************/
				for(a=0;a<40;a++){
					if(mfeature5[a]==0){
						for(b=a+1;b<40;b++){
							if(mfeature5[b]==0){
								for(c=0;c<8;c++){
									if (a==5*c){
										for(d=0;d<8;d++){
											if (b==5*d){
												feature1[c*(15-c)/2+d-c-1]=feature1[c*(15-c)/2+d-c-1]+1;
												a=b;
												flag5=1;
												if(flag == 1) {	break;}
											}
										}
									}
								}
								if(flag5==0){
									feature5[a*(79-a)/2+b-a-1]=feature5[a*(79-a)/2+b-a-1]+1;
									a=b;
									if(flag == 1) {	break;}
								}
								flag5=0;
							}
						}
					}
				}
/*****************
从水平开始以逆时针旋转搜索像素点的13*13领域的点，记录下是GMF的像素。
*****************/

				for(a=0;a<48;a++){
					if(mfeature6[a]==0){
						for(b=a+1;b<48;b++){
							if(mfeature6[b]==0){
								for(c=0;c<8;c++){
									if (a==6*c){
										for(d=0;d<8;d++){
											if (b==6*d){
												feature1[c*(15-c)/2+d-c-1]=feature1[c*(15-c)/2+d-c-1]+1;
												a=b;
												flag61=1;
												if(flag == 1) {	break;}
											}
										}
									}
								}
								for(c=0;c<16;c++){
									if (a==3*c){
										for(d=0;d<16;d++){
											if (b==3*d){
												if (flag61==0){
													feature2[c*(31-c)/2+d-c-1]=feature2[c*(31-c)/2+d-c-1]+1;
													a=b;
													flag62=1;
													if(flag == 1) {	break;}
												}
											}
										}
									}
								}
								for(c=0;c<24;c++){
									if (a==2*c){
										for(d=0;d<24;d++){
											if (b==2*d){
												if (flag61==0){
													feature3[c*(47-c)/2+d-c-1]=feature3[c*(47-c)/2+d-c-1]+1;
													a=b;
													flag63=1;
													if(flag == 1) {	break;}
												}
											}
										}
									}
								}
								if(flag61==0&&flag62==0&&flag63==0){
									feature6[a*(95-a)/2+b-a-1]=feature6[a*(95-a)/2+b-a-1]+1;
									a=b;
									if(flag == 1) {	break;}
								}
								flag61=0;
								flag62=0;
								flag63=0;
							}
						}
					}
				}

/*****************
从水平开始以逆时针旋转搜索像素点的15*15领域的点，记录下是GMF的像素。
*****************/
				for(a=0;a<56;a++){
					if(mfeature7[a]==0){
						for(b=a+1;b<56;b++){
							if(mfeature7[b]==0){
								for(c=0;c<8;c++){
									if (a==7*c){
										for(d=0;d<8;d++){
											if (b==7*d){
												feature1[c*(15-c)/2+d-c-1]=feature1[c*(15-c)/2+d-c-1]+1;
												a=b;
												flag7=1;
												if(flag == 1) {break;}
											}
										}
									}
								}
								if(flag7==0){
									feature7[a*(111-a)/2+b-a-1]=feature7[a*(111-a)/2+b-a-1]+1;
									a=b;
									if(flag == 1) {	break;}
								}
								flag7=0;
							}
						}
					}
				}
/*****************
从水平开始以逆时针旋转搜索像素点的17*17领域的点，记录下是GMF的像素。
*****************/
				for(a=0;a<64;a++){
					if(mfeature6[a]==0){
						for(b=a+1;b<64;b++){
							if(mfeature6[b]==0){
								for(c=0;c<8;c++){
									if (a==8*c){
										for(d=0;d<8;d++){
											if (b==8*d){
												feature1[c*(15-c)/2+d-c-1]=feature1[c*(15-c)/2+d-c-1]+1;
												a=b;
												flag81=1;
												if(flag == 1) {	break;}
											}
										}
									}
								}
								for(c=0;c<16;c++){
									if (a==4*c){
										for(d=0;d<16;d++){
											if (b==4*d){
												if (flag81==0){
													feature2[c*(31-c)/2+d-c-1]=feature2[c*(31-c)/2+d-c-1]+1;
													a=b;
													flag82=1;
													if(flag == 1) {	break;}
												}
											}
										}
									}
								}
								for(c=0;c<32;c++){
									if (a==2*c){
										for(d=0;d<32;d++){
											if (b==2*d){
												if (flag81==0&&flag82==0){
													feature4[c*(63-c)/2+d-c-1]=feature4[c*(63-c)/2+d-c-1]+1;
													a=b;
													flag83=1;
													if(flag == 1) {	break;}
												}
											}
										}
									}
								}

								if(flag81==0&&flag82==0&&flag83==0){
									feature8[a*(127-a)/2+b-a-1]=feature8[a*(127-a)/2+b-a-1]+1;
									a=b;
									if(flag == 1) {	break;}
								}
								flag81=0;
								flag82=0;
								flag83=0;
							}
						}
					}
				}
			}
		}
	}
	int a=0;
	for (a=0;a<28;a++){
		sum=sum+feature1[a];
	}
	for (a=0;a<120;a++){
		sum=sum+feature2[a];
	}
	for (a=0;a<276;a++){
		sum=sum+feature3[a];
	}
	for (a=0;a<496;a++){
		sum=sum+feature4[a];
	}
	for (a=0;a<780;a++){
		sum=sum+feature5[a];
	}

	for (a=0;a<1128;a++){
		sum=sum+feature6[a];
	}

	for (a=0;a<1540;a++){
		sum=sum+feature7[a];
	}
	for (a=0;a<2016;a++){
		sum=sum+feature8[a];
	}



	for(a=0;a<28;a++){
		//para[a]=feature1[a]/sum;
		probability.push_back(feature1[a]/sum);
	}
	for(a=0;a<120;a++){
		//para[a]=feature1[a]/sum;
		probability.push_back(feature2[a]/sum);
	}

	for(a=0;a<276;a++){
		//para[a]=feature1[a]/sum;
		probability.push_back(feature3[a]/sum);
	}
	for(a=0;a<496;a++){
		//para[a]=feature1[a]/sum;
		probability.push_back(feature4[a]/sum);
	}

	for(a=0;a<780;a++){
		//para[a]=feature1[a]/sum;
		probability.push_back(feature5[a]/sum);
	}

	for(a=0;a<1128;a++){
		//para[a]=feature1[a]/sum;
		probability.push_back(feature6[a]/sum);
	}

	for(a=0;a<1540;a++){
		//para[a]=feature1[a]/sum;
		probability.push_back(feature7[a]/sum);
	}
	for(a=0;a<2016;a++){
		//para[a]=feature1[a]/sum;
		probability.push_back(feature8[a]/sum);
	}

	// 复制图像
	memcpy(lpDIBBits, lpNewDIBBits, lWidth * lHeight);

	return probability;
}
