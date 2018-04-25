#include <cmath>
#include <stdio.h>
#include <stdlib.h>
#include "ISODATA.h"
//#include "ISODATA.h"
//#iclude "AddSeg.h"
#include "CString.h"
#include "AddProcess.h"
#include "recnum.h"

#include"android/log.h"
#define LOG_TAG "System.out"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

//��������������
//��ַ�����ֻ�
int Win_Postcode[POSTCODE_LEN];

int temp_wid;//��Ч��ͨԪƽ����
int temp_hei;//��Ч��ͨԪƽ����
int                    proImage_gray[2000*1000];
int                    proImage[2000*1000];
int                              showLen;
int                              showWid;

int                              index_Image;
float                             scale ;  //ͼ����12��ͼ��ı�
void initial()
{
	index_Image=0;
}
int max(int a,int b){return a>b?a:b;}
int min(int a,int b){return a<b?a:b;}
int  Otsu (int * proImage, int lWidth, int lHeight, int stax, int stay,
				  int dx, int dy)
{

	int Temp;      // ͼ������ֵ
	int thresholdValue=1; // ��ֵ
	int ihist[256];             // ͼ��ֱ��ͼ��256����

	int i, j, k;          // various counters
	int n, n1, n2, gmin, gmax;
	double m1, m2, sum, csum, fmax, sb;
	//int lLineBytes;
	//lLineBytes = WIDTHBYTES(lWidth * 8);
	// ��ֱ��ͼ����...
	memset(ihist, 0, sizeof(ihist));

	gmin=255; gmax=0;
  // ����ֱ��ͼ
	for (i = stay + 1; i < stay + dy - 1; i+=2)
	{
		for (j = stax + 1; j < stax + dx - 1; j+=2)
		{
			Temp=proImage[i*lWidth+j];

			ihist[Temp]++;
			if(Temp > gmax) gmax=Temp;
			if(Temp < gmin) gmin=Temp;

		}
	}

  // set up everything
	sum = csum = 0.0;
	n = 0;

	for (k = 0; k <= 255; k++)
	{
		sum += (double) k * (double) ihist[k];    /* x*f(x) ������*/
		n   += ihist[k];                                         /*  f(x)    ����    */

	}

	if (!n)
	{
		// if n has no value, there is problems...
		fprintf (stderr, "NOT NORMAL thresholdValue = 160\n");
		return (160);
	}

	// do the otsu global thresholding method
	fmax = -1.0;
	n1 = 0;
	for (k = 0; k < 255; k++)
	{
		n1 += ihist[k];
		if (!n1) { continue; }
		n2 = n - n1;
		if (n2 == 0) { break; }
		csum += (double) k *ihist[k];
		m1 = csum / n1;
		m2 = (sum - csum) / n2;
		sb = (double) n1 *(double) n2 *(m1 - m2) * (m1 - m2);
		/* bbg: note: can be optimized. */

		if (sb > fmax)
		{
			fmax = sb;
			thresholdValue = k-5;
		}
	}

	// at this point we have our thresholding value

	// debug code to display thresholding values
	/*if ( vvv & 1 )
	fprintf(stderr,"# OTSU: thresholdValue = %d gmin=%d gmax=%d\n",
     thresholdValue, gmin, gmax);*/

	return(thresholdValue);
}

//��OTSU�㷨��ͼ�������ֵ�任��ʵ�ֶ�ֵ��
BOOL  ThresholdOtsu(int * proImage, int lWidth, int lHeight,int backcolor)
{
	int Temp;
	int thresholdvalue=1;
	int i,j;
	//int lLineBytes;
    //lLineBytes = WIDTHBYTES(lWidth * 8);
	thresholdvalue=Otsu (proImage, lWidth, lHeight, 0, 0, lWidth, lHeight);
//	LOGI("er zhi hua, thresholdvalue=%d",thresholdvalue);
	if(backcolor!=0)//20100120;
	{
		if(backcolor-thresholdvalue<20)
			thresholdvalue=backcolor-20;
		if(backcolor-thresholdvalue>40)
			thresholdvalue=backcolor-40;
	}
	for(i=0; i<lHeight; i++)
	{
		for(j=0; j<lWidth; j++)
		{
			Temp =proImage[(lHeight-i-1)*lWidth+j];
			if(Temp<thresholdvalue)
			{
				proImage[(lHeight-i-1)*lWidth+j]=0;
			}
			else
			{
				proImage[(lHeight-i-1)*lWidth+j]=255;
			}
		}
	}
	return TRUE;
}





//��������
void Quick_Order(double * aaa,int left,int right,int * sn)
{
	int pivotpos=Partition(aaa,left,right,sn);
	if(left<pivotpos-1)
		Quick_Order(aaa,left,pivotpos-1,sn);
	if(pivotpos+1<right)
		Quick_Order(aaa,pivotpos+1,right,sn);
}
int Partition(double * aaa, int low,int high,int * sn)
{
	int i,k;
	double temp;
	int pivotpos=low;
	double pivot=aaa[low];
	for(i=low+1;i<=high;i++)
	{
		if(aaa[i]<pivot)
		{
			pivotpos++;
			if(pivotpos!=i)
			{
				temp=aaa[pivotpos];
				aaa[pivotpos]=aaa[i];
				aaa[i]=temp;
				k=sn[pivotpos];
				sn[pivotpos]=sn[i];
				sn[i]=k;
			}
		}
	}
	temp=aaa[pivotpos];
	aaa[pivotpos]=aaa[low];
	aaa[low]=temp;
	k=sn[pivotpos];
	sn[pivotpos]=sn[low];
	sn[low]=k;
	return pivotpos;
}


//extern "C" _declspec(dllexport) int recnum_jing(unsigned char* ImageBuffer,int width,int height);
//extern __declspec( dllexport) int recnum_jingsvm(unsigned char* ImageBuffer,int width,int height);


//�ֻ��зָ�
void post_segment(int* image,int width,character *chara_ori,character1 *chara_new1,int &num_c)
{
	int i;
	int left,right,wid,top,bottom;
	character1 chara_new[POSTCODE_LEN];
	if(num_c==POSTCODE_LEN)
	{
		for(i=0;i<num_c;i++)
		{
			chara_new[i].rect_c=chara_ori[i].rect_a;
			chara_new[i].type_c=1;
		}
	}
	else
	{
		top=1000;
		bottom=0;
		for(i=0;i<num_c;i++)
		{
			if(chara_ori[i].rect_a.top<top)
				top=chara_ori[i].rect_a.top;
			if(chara_ori[i].rect_a.bottom>bottom)
				bottom=chara_ori[i].rect_a.bottom;
		}

		left=chara_ori[0].rect_a.left-1;
		right=chara_ori[num_c-1].rect_a.right+1;
		wid=(right-left+1)/POSTCODE_LEN;
		for(i=0;i<POSTCODE_LEN;i++)
		{
			chara_new[i].rect_c.left=chara_ori[0].rect_a.left+i*wid;
			chara_new[i].rect_c.right=chara_ori[0].rect_a.left+(i+1)*wid;
			chara_new[i].rect_c.top=top;
			chara_new[i].rect_c.bottom=bottom;
			chara_new[i].type_c=1;
		}
		num_c=POSTCODE_LEN;
	}
	for(i=0;i<num_c;i++)
	{
		chara_new1[i].rect_c=chara_new[i].rect_c;
		chara_new1[i].type_c=chara_new[i].type_c;
//		DrawFrame(pDC,image,chara_new1[i].rect_c,2,RGB(200,60,20),width, chara_new1[i].rect_c.bottom-chara_new1[i].rect_c.top);

	}

}
//���ܣ��ͷ���ͬԪ�궨ʱ����������
void ReleaseList(runnode **HeadRun,int Length)
{

	runnode *current=NULL;
	runnode *precurrent=NULL;
	for(int i=0;i<Length;i++)//�ͷ��γ̱�
    {
		if(HeadRun[i]!=NULL)
		{
			current=HeadRun[i];
			while(current!=NULL)
			{
				precurrent=current;
				current=current->linenext;
				delete precurrent;
			}
		}
	}

}
//��ͨԪ�궨

int ConCompLabelling8(int *lpDIB, int lWidth, int lHeight, CodeComponent *rescomponent, BOOL imageflag)
{

	// ָ��Դͼ���ָ��
	int *pdibData;
	//��ͼ���ָ��
	int *pdata;
	pdata=(int *)malloc((lWidth+2)*(lHeight+2)*sizeof(int));
	// ͼ��ÿ�е��ֽ���
	int	lLineBytes;

	pdibData = lpDIB;
    lLineBytes = lWidth;

	int count;
	int i,j;
	int temp,nexttemp;
	runnode *current=NULL;
	runnode *nextcurrent=NULL;
	runnode *precurrent=NULL;
	runnode *preceding=NULL;

	component stack[16000];//��ͨԪ��ǩ��ջ
    runnode **headrun;//ÿ�е��γ�ָ��
	headrun=(runnode**)malloc((lHeight+2)*sizeof(runnode*));
	for(i=0;i<lHeight+2;i++)
		headrun[i]=NULL;
	for(i=0;i<1600;i++)//��ʼ����ջ
	{
		stack[i].firstrunnode=NULL;
		stack[i].lastrunnode=NULL;
		stack[i].value=255;
		stack[i].pixelnum=0;
	}

	// ��ʼ������pdata��ʼ************
	for(i=0;i<lHeight+1;i++)
	{
		for(j=0;j<lWidth+2;j++)
		{
			if(i==0)
				pdata[j]=255;
			else
			{
				if(j!=0&&j!=(lWidth+1)) //////////////////////////////////////
				{
					temp=lLineBytes*(i-1)+j-1;
					pdata[i*(lWidth+2)+j]=(int)pdibData[temp];
				}
				else
				{
					pdata[i*(lWidth+2)+j]=255;
				}
			}
		}
	}
	for(i=lHeight+1,j=0;j<lWidth+2;j++)
		pdata[i*(lWidth+2)+j]=255;
	//��ʼ������pdata����********************

	//��ʼ����ͨԪ��ǩ***********************
	count=1;
	for(i=1;i<lHeight+1;i++)
	{
		for(j=1;j<lWidth+1;j++)
		{
			temp=(lWidth+2)*i+j;
			if(pdata[temp]!=255)//��ǰ����Ϊǰ������
			{
				if((pdata[temp-1]+pdata[temp-lWidth-1]+pdata[temp-lWidth-2]+pdata[temp-lWidth-3])!=255*4)
				{
					if(pdata[temp-1]!=255)//�������Ϊǰ������
					{
						current->endx++;
						pdata[temp]=stack[pdata[temp-1]].value;
					}
					else if(pdata[temp-lWidth-3]!=255)//����
					{
						preceding=current;
						current=new runnode;
						if(current==NULL)
						{
							ReleaseList(headrun,lHeight+2);///zhoulijun add
							free(pdata);
							free(headrun);
							return 0;
						}
						if(headrun[i]==NULL)
						{
							headrun[i]=current;
							preceding=current;
						}
						else
							preceding->linenext=current;
						current->y=i;
						current->beginx=j;
						current->endx=j;
						current->linenext=NULL;
						current->next=NULL;
						if(stack[pdata[temp-lWidth-3]].lastrunnode==NULL)
						{
						//	AfxMessageBox("stack[pdata[temp-lWidth-3]].lastrunnode==NULL");
							ReleaseList(headrun,lHeight+2);///zhoulijun add
							free(pdata);
							free(headrun);
							return 0;

						}
						stack[pdata[temp-lWidth-3]].lastrunnode->next=current;
						stack[pdata[temp-lWidth-3]].lastrunnode=current;
						pdata[temp]=stack[pdata[temp-lWidth-3]].value;
					}//���Ͻ���

					else if(pdata[temp-lWidth-2]!=255)//��������
					{
						preceding=current;
						current=new runnode;
						if(current==NULL)
						{
							ReleaseList(headrun,lHeight+2);///zhoulijun add
							free(pdata);
							free(headrun);
							return 0;
						}
						if(headrun[i]==NULL)
						{
							headrun[i]=current;
							preceding=current;
						}
						else
							preceding->linenext=current;
						current->y=i;
						current->beginx=j;
						current->endx=j;
						current->linenext=NULL;
						current->next=NULL;
						if(stack[pdata[temp-lWidth-2]].lastrunnode==NULL)
						{
						//	AfxMessageBox("stack[pdata[temp-lWidth-2]].lastrunnode==NULL");

							ReleaseList(headrun,lHeight+2);///zhoulijun add
							free(pdata);
							free(headrun);
							return 0;
						}
						if(stack[pdata[temp-lWidth-2]].lastrunnode->next==NULL)
							stack[pdata[temp-lWidth-2]].lastrunnode->next=current;
						stack[pdata[temp-lWidth-2]].lastrunnode=current;
						pdata[temp]=stack[pdata[temp-lWidth-2]].value;
					}//���Ͻ���

					else if(pdata[temp-lWidth-1]!=255)//����
					{
						preceding=current;
						current=new runnode;
						if(current==NULL)
						{
							ReleaseList(headrun,lHeight+2);///zhoulijun add
							free(pdata);
							free(headrun);
							return 0;
						}
						if(headrun[i]==NULL)
						{
							headrun[i]=current;
							preceding=current;
						}
						else
							preceding->linenext=current;
						current->y=i;
						current->beginx=j;
						current->endx=j;
						current->linenext=NULL;
						current->next=NULL;
						if(stack[pdata[temp-lWidth-1]].lastrunnode==NULL)
						{
						//	AfxMessageBox("stack[pdata[temp-lWidth-1]].lastrunnode==NULL");
							ReleaseList(headrun,lHeight+2);///zhoulijun add
							free(pdata);
							free(headrun);
							return 0;
						}
						stack[pdata[temp-lWidth-1]].lastrunnode->next=current;
						stack[pdata[temp-lWidth-1]].lastrunnode=current;
						pdata[temp]=stack[pdata[temp-lWidth-1]].value;
					}//���Ͻ���
				}//end for (***if((pdata[temp-1]+pdata[temp-lWidth-1]+pdata[temp-lWidth-2]+pdata[temp-lWidth-3])!=255*4)***)
				else//��ʼһ���µ���ͨԪ
				{
					preceding=current;
					current=new runnode;
					if(current==NULL)
					{
						ReleaseList(headrun,lHeight+2);///zhoulijun add
						free(pdata);
						free(headrun);
						return 0;
					}
					if(headrun[i]==NULL)
					{
						headrun[i]=current;
						preceding=current;
					}
					else
						preceding->linenext=current;
					current->beginx=j;
					current->endx=j;
					current->y=i;
					current->linenext=NULL;
					current->next=NULL;
					pdata[temp]=count;
					count++;
					if(count>1599)
					{
						i=lHeight+2;
						j=lWidth+2;
					}
					if(count%255==0)
						count++;
					stack[pdata[temp]].compshape.minx=j;
					stack[pdata[temp]].firstrunnode=current;
					stack[pdata[temp]].value=pdata[temp];
					stack[pdata[temp]].lastrunnode=current;
				}
            }// end for (**if(pdata[temp]!=255)//��ǰ����Ϊǰ������**)
        }//end for (**for(j=1;j<lWidth+1;j++)**)
	}//end for (**for(i=1;i<lHeight+1;i++)**)
	//----------------------------------------------------
	int minx,maxx,miny,maxy;
	for(i=1;i<count;i++)
	{
		minx=lWidth;
		maxx=0;
		miny=lHeight;
		maxy=0;
		current=stack[i].firstrunnode;
		while(current!=NULL)
		{
			if(current->beginx<minx) minx=current->beginx;
			if(current->endx>maxx) maxx=current->endx;
			if(current->y<miny) miny=current->y;
			if(current->y>maxy) maxy=current->y;
			stack[i].pixelnum=stack[i].pixelnum+current->endx-current->beginx+1;
			current=current->next;
		}
		stack[i].compshape.minx=minx;
		stack[i].compshape.maxx=maxx;
		stack[i].compshape.miny=miny;
		stack[i].compshape.maxy=maxy;
		stack[i].sign=FALSE;
	}
	//---��ͨԪ��ǩ��ǽ���-----------

	//---�ϲ���ǩΪ��ͨԪ-------------

	int k;
	int index,nextindex;
	for(i=lHeight;i>1;i--)
	{
		if(headrun[i]!=NULL&&headrun[i-1]!=NULL)
		{
			current=headrun[i];
			nextcurrent=headrun[i-1];
			while(current!=NULL&&nextcurrent!=NULL)
			{

				if((current->beginx)>=(nextcurrent->endx+2)||(current->endx)<=(nextcurrent->beginx-2))
				{
					if(current->beginx>=nextcurrent->endx+2)
						nextcurrent=nextcurrent->linenext;
					else current=current->linenext;
				}

				else if(current->beginx+1<=nextcurrent->beginx)
				{
					temp=current->y*(lWidth+2)+current->beginx;
					nexttemp=nextcurrent->y*(lWidth+2)+nextcurrent->beginx;
					if(stack[pdata[temp]].sign==FALSE&&stack[pdata[nexttemp]].sign==FALSE)
					{
						if(stack[pdata[temp]].value!=stack[pdata[nexttemp]].value)
						{
							index=pdata[temp];//index��ʾ��ǰ�γ��е�����������ͨԪ�ĸ�������
							nextindex=pdata[nexttemp];//nextindex��ʾ��һ�е�ǰ�γ��е�����������ͨԪ�ĸ�������
							stack[nextindex].value=index;//��ʼ������һ���е��γ����ڵ�component������ǰ�γ����ڵ�component
							stack[nextindex].sign=TRUE;
							stack[index].pixelnum=stack[index].pixelnum+stack[nextindex].pixelnum;
							if(stack[index].compshape.minx>stack[nextindex].compshape.minx)
								stack[index].compshape.minx=stack[nextindex].compshape.minx;
							if(stack[index].compshape.maxx<stack[nextindex].compshape.maxx)
								stack[index].compshape.maxx=stack[nextindex].compshape.maxx;
							if(stack[index].compshape.miny>stack[nextindex].compshape.miny)
								stack[index].compshape.miny=stack[nextindex].compshape.miny;
							if(stack[index].compshape.maxy<stack[nextindex].compshape.maxy)
								stack[index].compshape.maxy=stack[nextindex].compshape.maxy;//����
						}
					}
					else if(stack[pdata[temp]].sign==FALSE&&stack[pdata[nexttemp]].sign==TRUE)
					{
						index=pdata[temp];
						nextindex=pdata[nexttemp];
						while(stack[nextindex].sign==TRUE)
						{
							nextindex=stack[nextindex].value;
						}
						if(stack[nextindex].value!=index)
						{
							stack[nextindex].value=index;//��ʼ����һ���е��γ����ڵ�component������ǰ�γ����ڵ�component
							stack[nextindex].sign=TRUE;
							stack[index].pixelnum=stack[index].pixelnum+stack[nextindex].pixelnum;
							if(stack[index].compshape.minx>stack[nextindex].compshape.minx)
								stack[index].compshape.minx=stack[nextindex].compshape.minx;
							if(stack[index].compshape.maxx<stack[nextindex].compshape.maxx)
								stack[index].compshape.maxx=stack[nextindex].compshape.maxx;
							if(stack[index].compshape.miny>stack[nextindex].compshape.miny)
								stack[index].compshape.miny=stack[nextindex].compshape.miny;
							if(stack[index].compshape.maxy<stack[nextindex].compshape.maxy)
								stack[index].compshape.maxy=stack[nextindex].compshape.maxy;
						}//����
					}
					else if(stack[pdata[temp]].sign==TRUE&&stack[pdata[nexttemp]].sign==FALSE)
					{
						index=pdata[temp];
						while(stack[index].sign==TRUE)
						{
							index=stack[index].value;
						}
						nextindex=pdata[nexttemp];
						if(stack[nextindex].value!=index)
						{
							stack[nextindex].value=index;//��ʼ������һ���е��γ����ڵ�component������ǰ�γ����ڵ�component
							stack[nextindex].sign=TRUE;
							stack[index].pixelnum=stack[index].pixelnum+stack[nextindex].pixelnum;
							if(stack[index].compshape.minx>stack[nextindex].compshape.minx)
								stack[index].compshape.minx=stack[nextindex].compshape.minx;
							if(stack[index].compshape.maxx<stack[nextindex].compshape.maxx)
								stack[index].compshape.maxx=stack[nextindex].compshape.maxx;
							if(stack[index].compshape.miny>stack[nextindex].compshape.miny)
								stack[index].compshape.miny=stack[nextindex].compshape.miny;
							if(stack[index].compshape.maxy<stack[nextindex].compshape.maxy)
								stack[index].compshape.maxy=stack[nextindex].compshape.maxy;//����
						}
					}
					else
					{
						if(stack[pdata[temp]].value!=stack[pdata[nexttemp]].value)
						{
							index=pdata[temp];
							while(stack[index].sign==TRUE)
							{
								index=stack[index].value;
							}
							nextindex=pdata[nexttemp];
							while(stack[nextindex].sign==TRUE)
							{
								nextindex=stack[nextindex].value;
							}
							if(stack[nextindex].value!=index)
							{
								stack[nextindex].value=index;//��ʼ����һ���е��γ����ڵ�component������ǰ�γ����ڵ�component
								stack[nextindex].sign=TRUE;
								stack[index].pixelnum=stack[index].pixelnum+stack[nextindex].pixelnum;
								if(stack[index].compshape.minx>stack[nextindex].compshape.minx)
									stack[index].compshape.minx=stack[nextindex].compshape.minx;
								if(stack[index].compshape.maxx<stack[nextindex].compshape.maxx)
									stack[index].compshape.maxx=stack[nextindex].compshape.maxx;
								if(stack[index].compshape.miny>stack[nextindex].compshape.miny)
									stack[index].compshape.miny=stack[nextindex].compshape.miny;
								if(stack[index].compshape.maxy<stack[nextindex].compshape.maxy)
									stack[index].compshape.maxy=stack[nextindex].compshape.maxy;
							}
						}//����
					}


					if(current->endx<=nextcurrent->endx)
					{
						current=current->linenext;
					//	nextcurrent=nextcurrent->linenext;
					}
					else nextcurrent=nextcurrent->linenext;
				}//end for (**else if(current->beginx+1<=nextcurrent->beginx)**)

				else if(current->beginx>=nextcurrent->beginx-1)//��ǩ���ʱ�Ѿ����ǵ�
				{
					if(current->endx<=nextcurrent->endx)
					{
						current=current->linenext;
					//	nextcurrent=nextcurrent->linenext;
					}
					else
					{
						nextcurrent=nextcurrent->linenext;
					}

				}
			}//end for (**while(current!=NULL&&nextcurrent!=NULL)**)

		}//end for (**if(headrun[i]!=NULL&&headrun[i-1]!=NULL)**)
	}// ��ͨԪ�궨����



	//�˳������Լ�������̫�ٵ���ͨԪ------------------------------------------------------
	int SmallThresh;
	SmallThresh=15;

	int precdeletenum;//�˳�����ǰ����ͨԱ����
	for(i=1,k=0;i<count;i++)//�Ѹ���ͨԪ�ĸ���������ȡ���������pstack�����У��Լ��ٺ�����ʣ�������ʱ��
	{
		if(i%255==0)
			stack[i].sign=TRUE;
		if(stack[i].sign==FALSE)
		{
			if(stack[i].compshape.maxy-stack[i].compshape.miny<=1||stack[i].compshape.maxx-stack[i].compshape.minx<2)
			{
				stack[i].sign=TRUE;
				stack[i].value=255;
			}
			else
				k++;
		}
	}
	for(i=1,precdeletenum=0;i<count;i++)//�Ѹ���ͨԪ�ĸ���������ȡ���������pstack�����У��Լ��ٺ�����ʣ�������ʱ��
	{
		if(stack[i].sign==FALSE&&stack[i].pixelnum>SmallThresh)

		{
			rescomponent[precdeletenum].sign=FALSE;
			rescomponent[precdeletenum].pixelnum=stack[i].pixelnum;
			rescomponent[precdeletenum].compshape.maxx=stack[i].compshape.maxx-1;
			rescomponent[precdeletenum].compshape.minx=stack[i].compshape.minx-1;
			rescomponent[precdeletenum].compshape.maxy=stack[i].compshape.maxy-1;
			rescomponent[precdeletenum].compshape.miny=stack[i].compshape.miny-1;
			rescomponent[precdeletenum].value=stack[i].value;
			precdeletenum++;
		}
		if(precdeletenum>199)
			break;
	}

	//��ͼ���е�ǰ�����ظ�ֵΪ��������ͨԪ�ı�־ֵ������������Ӿ����е���������
/*	runnode *p;
	for(i=1;i<count;i++)
	{
		p=stack[i].firstrunnode;//
		while(p!=NULL)
		{
			for(j=p->beginx;j<=p->endx;j++)
			{
				temp=p->y*(lWidth+2)+j;
				index=pdata[temp];
				while(stack[index].sign==TRUE&&index!=255)
				{
					index=stack[index].value;
				}
				if(index==255) pdata[temp]=255;
				else
					pdata[temp]=index;


			}
			p=p->next;
		}
	}
	int anothertemp;
	for(i=1;i<lHeight+1;i++)
	{
		for(j=1;j<lWidth+1;j++)
		{
			temp=lLineBytes*(i-1)+j-1;
			anothertemp=i*(lWidth+2)+j;
			if(pdata[anothertemp]%255!=0&&pdata[anothertemp]!=0)
  	             pdibData[temp]=(BYTE)pdata[anothertemp]%255;
			else
				pdibData[temp]=(BYTE)pdata[anothertemp];
		}

	}*/
	ReleaseList(headrun,lHeight+2);///�ͷ��γ̱�
	free(pdata);
	free(headrun);
	return precdeletenum;
}

//�ϲ���ͨԪΪ����
void Comp_in(int * image, int lWidth, int lHeight, CodeComponent *rescomponent, int& digi)
{
	int i,j;

	int digi_new=0;
	CodeComponent rescomponent1[200];

	//****�ϲ��ཻ�������ϵ����ͨԪ***************
	for(i=0;i<digi;i++)
	{
		if(rescomponent[i].sign==TRUE)
			continue;
		for(j=0;j<digi;j++)
		{
			if(i!=j&&rescomponent[j].sign==FALSE)
			{
				if(rescomponent[i].compshape.maxx-rescomponent[i].compshape.minx+1+rescomponent[j].compshape.maxx-rescomponent[j].compshape.minx+1
					>max(rescomponent[i].compshape.maxx,rescomponent[j].compshape.maxx)-min(rescomponent[i].compshape.minx,rescomponent[j].compshape.minx)+1
					&&rescomponent[i].compshape.maxy-rescomponent[i].compshape.miny+1+rescomponent[j].compshape.maxy-rescomponent[j].compshape.miny+1
					>max(rescomponent[i].compshape.maxy,rescomponent[j].compshape.maxy)-min(rescomponent[i].compshape.miny,rescomponent[j].compshape.miny)+1)
				//	&&max(rescomponent[i].compshape.maxx,rescomponent[j].compshape.maxx)-min(rescomponent[i].compshape.minx,rescomponent[j].compshape.minx)+1<temp_wid+10
				//	&&max(rescomponent[i].compshape.maxy,rescomponent[j].compshape.maxy)-min(rescomponent[i].compshape.miny,rescomponent[j].compshape.miny)+1<temp_hei+10)
				{
					if(i>j)//20090922�����޸�
					{
						rescomponent[i].sign=TRUE;
						rescomponent[j].compshape.minx=min(rescomponent[i].compshape.minx,rescomponent[j].compshape.minx);
						rescomponent[j].compshape.maxx=max(rescomponent[i].compshape.maxx,rescomponent[j].compshape.maxx);
						rescomponent[j].compshape.miny=min(rescomponent[i].compshape.miny,rescomponent[j].compshape.miny);
						rescomponent[j].compshape.maxy=max(rescomponent[i].compshape.maxy,rescomponent[j].compshape.maxy);
						rescomponent[j].pixelnum=rescomponent[i].pixelnum+rescomponent[j].pixelnum;
						i=j-1;
						break;
					}
					else
					{
						rescomponent[j].sign=TRUE;
						rescomponent[i].compshape.minx=min(rescomponent[i].compshape.minx,rescomponent[j].compshape.minx);
						rescomponent[i].compshape.maxx=max(rescomponent[i].compshape.maxx,rescomponent[j].compshape.maxx);
						rescomponent[i].compshape.miny=min(rescomponent[i].compshape.miny,rescomponent[j].compshape.miny);
						rescomponent[i].compshape.maxy=max(rescomponent[i].compshape.maxy,rescomponent[j].compshape.maxy);
						rescomponent[i].pixelnum=rescomponent[i].pixelnum+rescomponent[j].pixelnum;
						break;
					}
				}
			}
		}
	}

	for(i=0;i<digi;i++)
	{
		if(rescomponent[i].sign==FALSE)
		{
		//	digi_new++;
			rescomponent1[digi_new].sign=FALSE;
			rescomponent1[digi_new].compshape.minx=rescomponent[i].compshape.minx;
			rescomponent1[digi_new].compshape.maxx=rescomponent[i].compshape.maxx;
			rescomponent1[digi_new].compshape.miny=rescomponent[i].compshape.miny;
			rescomponent1[digi_new].compshape.maxy=rescomponent[i].compshape.maxy;
			rescomponent1[digi_new].value=rescomponent[i].value;
			rescomponent1[digi_new].pixelnum=rescomponent[i].pixelnum;
			digi_new++;
		}
		if(digi_new>199)
			break;
	}
	digi=digi_new;
	//*****�ϲ��ཻ�������ϵ����ͨԪ******����*******

	for(i=0;i<digi;i++)
	{

		rescomponent[i].sign=FALSE;
		rescomponent[i].compshape.minx=rescomponent1[i].compshape.minx;
		rescomponent[i].compshape.maxx=rescomponent1[i].compshape.maxx;
		rescomponent[i].compshape.miny=rescomponent1[i].compshape.miny;
		rescomponent[i].compshape.maxy=rescomponent1[i].compshape.maxy;
		rescomponent[i].value=rescomponent1[i].value;
		rescomponent[i].pixelnum=rescomponent1[i].pixelnum;

	}

}

//�Թ�����ͨԪ���½��д���
void reprocess(int *image, int lWidth, int lHeight, CodeComponent *rescomponent, int& digi)
{
	int i,j,k;
	int Hist_wid[200];//��ͨԪ���ֱ��ͼ
	int Hist_hei[200];//��ͨԪ�߶�ֱ��ͼ
	memset(Hist_wid, 0, 200*sizeof(int));
	memset(Hist_hei, 0, 200*sizeof(int));
	int wid,hei;//��ͨԪ���
	int num_wid, num_hei;//���к����ߵ���ͨԪ�ĸ���
	num_wid=0;
	num_hei=0;
	CRect rect;

	//*******ͳ����Ч��ͨԪ�Ŀ��*********************
	for(i=0;i<digi;i++)
	{
		rect.left=rescomponent[i].compshape.minx;
		rect.right=rescomponent[i].compshape.maxx;
		rect.top=rescomponent[i].compshape.miny;
		rect.bottom=rescomponent[i].compshape.maxy;
		wid=rect.right-rect.left+1;
		hei=rect.bottom-rect.top+1;
		if(wid<(int)(200*scale)&&wid>(int)(15*scale)&&wid<hei*7/4&& (float)rescomponent[i].pixelnum/(float)(wid*hei)<0.7)//(200910)
		{
			Hist_wid[wid]++;
			num_wid++;
		}
		if(hei<(int)(200*scale)&&hei>(int)(10*scale)&& (float)rescomponent[i].pixelnum/(float)(wid*hei)<0.7)//(200910)
		{
			Hist_hei[hei]++;
			num_hei++;
		}

//		DrawFrame(pDC,proImage,rect,2,RGB(0,0,255),ImageLength, ImageWidth);
	}

	int sum_wid=0;
	temp_wid=0;
	for(i=199;i>0;i--)
	{
		sum_wid+=Hist_wid[i];
		if(sum_wid>num_wid/3||(sum_wid>num_wid/4&&Hist_wid[i-1]==0&&Hist_wid[i-2]==0))
		{
			temp_wid=i;
			break;
		}
	}
	int sum_hei=0;
	temp_hei=0;
	for(i=199;i>0;i--)
	{
		sum_hei+=Hist_hei[i];
		if(sum_hei>num_hei/3)
		{
			temp_hei=i;
			break;
		}
	}
	if((double)temp_wid/temp_hei<4.0/5&&temp_wid<35)
		temp_wid=temp_hei*4/5;
	else if((double)temp_wid/temp_hei<4.0/5)
		temp_hei=temp_wid*5/4;
	else if(temp_wid>temp_hei)
		temp_wid=temp_hei;
	//*******ͳ����Ч��ͨԪ�Ŀ��******����*********


	//********���±�־�ϴ���ͨԪ����****************
	int *image1;
	int digi1=0;
	CodeComponent rescomponent0[200];
	int digi_ori=digi;
	for(i=0;i<digi_ori;i++)
	{
		rect.left=rescomponent[i].compshape.minx;
		rect.right=rescomponent[i].compshape.maxx;
		rect.top=rescomponent[i].compshape.miny;
		rect.bottom=rescomponent[i].compshape.maxy;
		wid=rect.right-rect.left+1;
		hei=rect.bottom-rect.top+1;
		if(wid/hei>5&&wid>3*temp_wid)//ȥ������
		{
			rescomponent[i].sign=TRUE;
			continue;
		}
		if(hei/wid>5&&hei>3*temp_hei)//ȥ������
		{
			rescomponent[i].sign=TRUE;
			continue;
		}
		if(wid>temp_wid*4/3||hei>temp_hei*4/3)
		{
			image1 = (int*)malloc(wid*hei*sizeof(int));
			for(j=0;j<hei;j++)
			{
				for(k=0;k<wid;k++)
				{
					image1[j*wid+k]=image[(j+rect.top)*lWidth+k+rect.left];
				}
			}
			ThresholdOtsu(image1, wid, hei,0);
			digi1=0;

			digi1=ConCompLabelling8(image1, wid, hei,rescomponent0,FALSE);
			Comp_in(image1, wid, hei, rescomponent0, digi1);
			rescomponent[i].sign=TRUE;
			if(digi+digi1>199)//20091117
			{
				free(image1);
				continue;
			}
			for(j=digi;j<digi+digi1;j++)
			{
				rescomponent[j].sign=rescomponent0[j-digi].sign;
				rescomponent[j].compshape.minx=rescomponent0[j-digi].compshape.minx+rect.left;
				rescomponent[j].compshape.maxx=rescomponent0[j-digi].compshape.maxx+rect.left;
				rescomponent[j].compshape.miny=rescomponent0[j-digi].compshape.miny+rect.top;
				rescomponent[j].compshape.maxy=rescomponent0[j-digi].compshape.maxy+rect.top;
				rescomponent[j].value=rescomponent0[j-digi].value;
				rescomponent[j].pixelnum=rescomponent0[j-digi].pixelnum;
			}
			digi=digi+digi1;
			free(image1);
		}
	}
	//*********���±�־�ϴ���ͨԪ����****����******

	//���ų��߶Ƚϴ�
	for(i=0;i<digi;i++)
	{
		if(rescomponent[i].sign==FALSE)
		{
			rect.left=rescomponent[i].compshape.minx;
			rect.right=rescomponent[i].compshape.maxx;
			rect.top=rescomponent[i].compshape.miny;
			rect.bottom=rescomponent[i].compshape.maxy;
			wid=rect.right-rect.left+1;
			hei=rect.bottom-rect.top+1;
			if(hei>temp_hei*2)//||rescomponent[i].pixelnum<35)
				rescomponent[i].sign=TRUE;

		}
	}

}

//�зָ�
//�зָ�
int row_segment(int *image, int lWidth, int lHeight, CodeComponent *rescomponent, int& digi, CodeComponent row_rescomponent[Num_line][Num_cha],int *row_x)
{
	int i,j;
	int top[Num_line];
	int bottom[Num_line];
	int *image_y;
	image_y=(int *)malloc(lHeight*sizeof(int));
	if(image_y==NULL)
		return -1;
	memset(image_y,0,lHeight*sizeof(int));

	//*********����ȷ��ÿ�е��ϱ�Ե���±�Ե************
	for(i=0;i<digi;i++)//��ͨԪ����ͶӰ
	{
		if(rescomponent[i].sign==FALSE)
		{
			for(j=rescomponent[i].compshape.miny;j<rescomponent[i].compshape.maxy;j++)
			{
				image_y[j]+=rescomponent[i].compshape.maxx-rescomponent[i].compshape.minx+1;
			}
		}
	}
	bool lab_top=false;
	bool lab_bottom=false;
	j=0;
	for(i=0;i<lHeight-1;i++)
	{
		if(image_y[i]>(int)(10*scale)&&image_y[i+1]>(int)(10*scale)&&lab_top==false)
		{
			top[j]=(i-3)<0?0:(i-3);
			lab_top=true;
		}
		if(image_y[i]<(int)(10*scale)&&image_y[i]<(int)(10*scale)&&lab_bottom==false)
		{
			bottom[j]=(i+2)>lHeight-1?lHeight-1:(i+2);
			lab_bottom=true;
		}
		if(i==lHeight-2&&lab_top==true)//��ַ���±߽紦��
		{
			bottom[j]=lHeight-1;
			lab_bottom=true;
		}
		if(lab_top==true&&lab_bottom==true)
		{
			lab_top=false;
			lab_bottom=false;
			if(bottom[j]-top[j]>(int)(15*scale))
				j++;
		}
		if(j>Num_line-1)
			break;
	}
	//******����ȷ��ÿ�е��ϱ�Ե���±�Ե*****����*******

	//*******����ͨԪ����****************
	int row_y_ori=j;
	int row_y=0;//�б�
	CodeComponent rescomponent_temp[200];
	int num=0;
	int num_r=0;
	for(i=0;i<row_y_ori;i++)//���������
	{
		if(row_y_ori>2&&i==0&&top[i]==0)
			continue;
		if(row_y>Num_line-1)
			break;
		if(bottom[i]-top[i]<temp_hei/2)//�ǵ�ַ��
			continue;
		else if((bottom[i]-top[i]<temp_hei*3/2&&temp_hei>temp_wid)||(bottom[i]-top[i]<temp_hei*3/2&&temp_hei<=temp_wid))//����
		{
			for(j=0;j<digi;j++)
			{
				if(rescomponent[j].sign==FALSE&&top[i]-rescomponent[j].compshape.miny<3//20161021
					&&rescomponent[j].compshape.maxy-bottom[i]<3)
				{
					row_rescomponent[row_y][row_x[row_y]].compshape.minx=rescomponent[j].compshape.minx;
					row_rescomponent[row_y][row_x[row_y]].compshape.maxx=rescomponent[j].compshape.maxx;
					row_rescomponent[row_y][row_x[row_y]].compshape.miny=rescomponent[j].compshape.miny;
					row_rescomponent[row_y][row_x[row_y]].compshape.maxy=rescomponent[j].compshape.maxy;
					row_rescomponent[row_y][row_x[row_y]].sign=FALSE;
					row_rescomponent[row_y][row_x[row_y]].pixelnum=rescomponent[j].pixelnum;
					rescomponent[j].sign=TRUE;
					row_x[row_y]++;
					if(row_x[row_y]>Num_cha-1)
						break;
				}
			}
		//	if(row_x[row_y]>0)
			Comp_Order(row_rescomponent[row_y], row_x[row_y]);//����
			row_y++;
		}
		else//�ǵ��� //if(bottom[i]-top[i]<temp_hei*7/3)//˫��
		{

			num_r=(bottom[i]-top[i])/temp_hei;
			num=0;
			for(j=0;j<digi;j++)//(rescomponent[i].compshape.miny>top[i]&&rescomponent[i].compshape.maxy<bottom[i])||
			{
				if(rescomponent[j].sign==FALSE&&rescomponent[j].compshape.miny-top[i]>-temp_hei/2&&(
					(rescomponent[j].compshape.miny-top[i]<temp_hei*num_r&&rescomponent[j].compshape.maxy-bottom[i]<temp_hei)||
					(rescomponent[j].compshape.miny>=top[i]&&rescomponent[j].compshape.maxy<=bottom[i])||
					(rescomponent[j].compshape.maxy-rescomponent[j].compshape.miny<8&&rescomponent[j].compshape.maxy<min(bottom[i],top[i+1]))))
				{
					rescomponent[j].sign=TRUE;
					rescomponent_temp[num].sign=FALSE;
					rescomponent_temp[num].pixelnum=rescomponent[j].pixelnum;
					rescomponent_temp[num].compshape.minx=rescomponent[j].compshape.minx;
					rescomponent_temp[num].compshape.miny=rescomponent[j].compshape.miny;
					rescomponent_temp[num].compshape.maxx=rescomponent[j].compshape.maxx;
					rescomponent_temp[num].compshape.maxy=rescomponent[j].compshape.maxy;
					num++;
				}
			}
			//����
			Comp_Order(rescomponent_temp, num);
			//����
			row_segment1(rescomponent_temp, num,row_rescomponent,row_y, row_x,num_r);



		}

	}
	//*******��ͨԪ����******����**********


	free(image_y);
	return row_y;
}

//��ͨԪ����������
void Comp_Order(CodeComponent *rescomponent, int Comp_num)
{
	int j;
	double *comp_minx;
	int *sn;
	CodeComponent rescomponent_temp[200];
	comp_minx=(double*)malloc(Comp_num*sizeof(double));
	sn=(int*)malloc(Comp_num*sizeof(int));
	for(j=0;j<Comp_num;j++)
	{
		comp_minx[j]=(double)rescomponent[j].compshape.minx;
		sn[j]=j;
	}
	Quick_Order(comp_minx,0,Comp_num-1,sn);
	for(j=0;j<Comp_num;j++)
	{
		rescomponent_temp[j].pixelnum=rescomponent[sn[j]].pixelnum;
		rescomponent_temp[j].compshape.minx=rescomponent[sn[j]].compshape.minx;
		rescomponent_temp[j].compshape.maxx=rescomponent[sn[j]].compshape.maxx;
		rescomponent_temp[j].compshape.miny=rescomponent[sn[j]].compshape.miny;
		rescomponent_temp[j].compshape.maxy=rescomponent[sn[j]].compshape.maxy;
	}
	for(j=0;j<Comp_num;j++)
	{
		rescomponent[j].pixelnum=rescomponent_temp[j].pixelnum;
		rescomponent[j].compshape.minx=rescomponent_temp[j].compshape.minx;
		rescomponent[j].compshape.maxx=rescomponent_temp[j].compshape.maxx;
		rescomponent[j].compshape.miny=rescomponent_temp[j].compshape.miny;
		rescomponent[j].compshape.maxy=rescomponent_temp[j].compshape.maxy;
	}
	free(comp_minx);
	free(sn);
}
//TODO  //�ϲ����ֲ���
void parts_merge(CodeComponent row_rescomponent[Num_line][Num_cha],int *row_x,int row_y)
{
	int i,j,k;
	CodeComponent rescomponent_temp;
	for(i=0;i<row_y;i++)
	{
		if(i==0&&row_x[i]<12&&row_x[i]>7)//�ж��Ƿ�Ϊ�ֻ���
		{
			k=0;
			for(j=0;j<row_x[i];j++)
			{
				if(row_rescomponent[i][j].sign==FALSE&&(row_rescomponent[i][j].compshape.maxx-row_rescomponent[i][j].compshape.minx+1)/
					(row_rescomponent[i][j].compshape.maxy-row_rescomponent[i][j].compshape.miny+1)<0.7)
				{
					k++;
				}
			}
			if(k>10)
				k=11;
			else
			{
				k=0;
				for(j=0;j<row_x[i];j++)
				{
					if((row_rescomponent[i][j].compshape.maxx-row_rescomponent[i][j].compshape.minx+1)<temp_wid)
					{
						k++;
					}
				}
				if(k==row_x[i])
					k=11;
				else
				{
					k=0;
					for(j=0;j<row_x[i];j++)
					{
						if((row_rescomponent[i][j].compshape.maxy-row_rescomponent[i][j].compshape.miny+1)<temp_hei)
						{
							k++;
						}
					}
					if(k==row_x[i]&&(row_rescomponent[i][row_x[i]-1].compshape.maxx-row_rescomponent[i][0].compshape.minx+1)/11<temp_wid
						&&(row_rescomponent[i][row_x[i]-1].compshape.maxx-row_rescomponent[i][0].compshape.minx+1)/11>temp_wid/2)
						k=11;
				}
			}
			if(k==11)
				continue;

		}

	/*	for(j=0;j<row_x[i];j++)//�����һ�������һ�����ֲ���
		{
			if(j==0&&max(row_rescomponent[i][j].compshape.maxx,row_rescomponent[i][j+1].compshape.maxx)-
				min(row_rescomponent[i][j].compshape.minx,row_rescomponent[i][j+1].compshape.minx)<temp_wid*13/10&&
				max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j+1].compshape.maxy)-
				min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j+1].compshape.miny)<temp_hei*12/10+2)
			{
				if(row_rescomponent[i][j+1].compshape.minx>row_rescomponent[i][j].compshape.maxx&&max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j+1].compshape.maxy)-
					min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j+1].compshape.miny)<(temp_hei*5/6-1)
					&&max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j+1].compshape.maxy)-
					min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j+1].compshape.miny)>temp_hei/2)
					continue;//�ܿ���������
				else if(row_rescomponent[i][j+1].compshape.minx-row_rescomponent[i][j].compshape.maxx>temp_wid/8
							&&max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j+1].compshape.maxy)-
						min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j+1].compshape.miny)<temp_hei*9/10)
							continue;
				row_rescomponent[i][j].sign=TRUE;
				row_rescomponent[i][j+1].pixelnum=row_rescomponent[i][j].pixelnum+row_rescomponent[i][j+1].pixelnum;
				row_rescomponent[i][j+1].compshape.maxx=max(row_rescomponent[i][j].compshape.maxx,row_rescomponent[i][j+1].compshape.maxx);
				row_rescomponent[i][j+1].compshape.minx=min(row_rescomponent[i][j].compshape.minx,row_rescomponent[i][j+1].compshape.minx);
				row_rescomponent[i][j+1].compshape.maxy=max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j+1].compshape.maxy);
				row_rescomponent[i][j+1].compshape.miny=min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j+1].compshape.miny);
			}

		}*/
		for(j=1;j<row_x[i]-1;j++)
		{
			if(row_rescomponent[i][j].compshape.maxx-row_rescomponent[i][j].compshape.minx+1<temp_wid*2/3
				||row_rescomponent[i][j].compshape.maxy-row_rescomponent[i][j].compshape.miny+1<temp_hei/2)
			{
			/*	if(row_rescomponent[i][j].compshape.minx>row_rescomponent[i][j-1].compshape.maxx)
				{
					if(max(row_rescomponent[i][j].compshape.maxx,row_rescomponent[i][j-1].compshape.maxx)-
						min(row_rescomponent[i][j].compshape.minx,row_rescomponent[i][j-1].compshape.minx)<temp_wid*12/10&&
						max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j-1].compshape.maxy)-
						min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j-1].compshape.miny)<temp_hei*12/10)
					{
						row_rescomponent[i][j-1].sign=TRUE;
						row_rescomponent[i][j].compshape.maxx=max(row_rescomponent[i][j].compshape.maxx,row_rescomponent[i][j-1].compshape.maxx);
						row_rescomponent[i][j].compshape.minx=min(row_rescomponent[i][j].compshape.minx,row_rescomponent[i][j-1].compshape.minx);
						row_rescomponent[i][j].compshape.maxy=max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j-1].compshape.maxy);
						row_rescomponent[i][j].compshape.miny=min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j-1].compshape.miny);
					}
				}
				else if(row_rescomponent[i][j+1].compshape.minx>row_rescomponent[i][j].compshape.maxx)
				{
					if(max(row_rescomponent[i][j].compshape.maxx,row_rescomponent[i][j+1].compshape.maxx)-
						min(row_rescomponent[i][j].compshape.minx,row_rescomponent[i][j+1].compshape.minx)<temp_wid*12/10&&
						max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j+1].compshape.maxy)-
						min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j+1].compshape.miny)<temp_hei*12/10)
					{
						row_rescomponent[i][j].sign=TRUE;
						row_rescomponent[i][j+1].compshape.maxx=max(row_rescomponent[i][j].compshape.maxx,row_rescomponent[i][j+1].compshape.maxx);
						row_rescomponent[i][j+1].compshape.minx=min(row_rescomponent[i][j].compshape.minx,row_rescomponent[i][j+1].compshape.minx);
						row_rescomponent[i][j+1].compshape.maxy=max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j+1].compshape.maxy);
						row_rescomponent[i][j+1].compshape.miny=min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j+1].compshape.miny);
					}
				}
				else */

				if(((row_rescomponent[i][j-1].compshape.maxx-row_rescomponent[i][j-1].compshape.minx+1)*
					(row_rescomponent[i][j-1].compshape.maxy-row_rescomponent[i][j-1].compshape.miny+1)<
					(row_rescomponent[i][j+1].compshape.maxx-row_rescomponent[i][j+1].compshape.minx+1)*
					(row_rescomponent[i][j+1].compshape.maxy-row_rescomponent[i][j+1].compshape.miny+1))||
					(row_rescomponent[i][j].compshape.minx-row_rescomponent[i][j-1].compshape.maxx<row_rescomponent[i][j+1].compshape.minx-row_rescomponent[i][j].compshape.maxx))
				{
					//�ų����ֻ���ĸ�ϲ�
					if(max(row_rescomponent[i][j].compshape.maxx,row_rescomponent[i][j-1].compshape.maxx)-
						min(row_rescomponent[i][j].compshape.minx,row_rescomponent[i][j-1].compshape.minx)<temp_wid*12/10&&
						max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j-1].compshape.maxy)-
						min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j-1].compshape.miny)<temp_hei*12/10+2)
					{
						if(row_rescomponent[i][j].compshape.minx>row_rescomponent[i][j-1].compshape.maxx&&max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j-1].compshape.maxy)-
						min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j-1].compshape.miny)<(temp_hei*5/6-1)
						&&max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j-1].compshape.maxy)-
						min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j-1].compshape.miny)>temp_hei/2)
							continue;
						//�Բ�����������
						else if(row_rescomponent[i][j].compshape.minx-row_rescomponent[i][j-1].compshape.maxx>temp_wid/8
							&&max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j-1].compshape.maxy)-
						min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j-1].compshape.miny)<temp_hei*10/11)
							continue;
						row_rescomponent[i][j-1].sign=TRUE;
						row_rescomponent[i][j].pixelnum=row_rescomponent[i][j].pixelnum+row_rescomponent[i][j-1].pixelnum;
						row_rescomponent[i][j].compshape.maxx=max(row_rescomponent[i][j].compshape.maxx,row_rescomponent[i][j-1].compshape.maxx);
						row_rescomponent[i][j].compshape.minx=min(row_rescomponent[i][j].compshape.minx,row_rescomponent[i][j-1].compshape.minx);
						row_rescomponent[i][j].compshape.maxy=max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j-1].compshape.maxy);
						row_rescomponent[i][j].compshape.miny=min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j-1].compshape.miny);
					/*	row_rescomponent[i][j-1].compshape.maxx=max(row_rescomponent[i][j].compshape.maxx,row_rescomponent[i][j-1].compshape.maxx);
						row_rescomponent[i][j-1].compshape.minx=min(row_rescomponent[i][j].compshape.minx,row_rescomponent[i][j-1].compshape.minx);
						row_rescomponent[i][j-1].compshape.maxy=max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j-1].compshape.maxy);
						row_rescomponent[i][j-1].compshape.miny=min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j-1].compshape.miny);
					*/
					}
				}
				else
				{
					if(max(row_rescomponent[i][j].compshape.maxx,row_rescomponent[i][j+1].compshape.maxx)-
						min(row_rescomponent[i][j].compshape.minx,row_rescomponent[i][j+1].compshape.minx)<temp_wid*12/10&&
						max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j+1].compshape.maxy)-
						min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j+1].compshape.miny)<temp_hei*12/10+2)
					{
						//�ų����ֻ���ĸ�ϲ�
						if(row_rescomponent[i][j+1].compshape.minx>row_rescomponent[i][j].compshape.maxx&&max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j+1].compshape.maxy)-
						min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j+1].compshape.miny)<(temp_hei*5/6-1)
						&&max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j+1].compshape.maxy)-
						min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j+1].compshape.miny)>temp_hei/2)
							continue;
						//�Բ�����������
						else if(row_rescomponent[i][j+1].compshape.minx-row_rescomponent[i][j].compshape.maxx>temp_wid/8
							&&max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j+1].compshape.maxy)-
						min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j+1].compshape.miny)<temp_hei*10/11)
							continue;
						row_rescomponent[i][j].sign=TRUE;
						row_rescomponent[i][j+1].pixelnum=row_rescomponent[i][j].pixelnum+row_rescomponent[i][j+1].pixelnum;
						row_rescomponent[i][j+1].compshape.maxx=max(row_rescomponent[i][j].compshape.maxx,row_rescomponent[i][j+1].compshape.maxx);
						row_rescomponent[i][j+1].compshape.minx=min(row_rescomponent[i][j].compshape.minx,row_rescomponent[i][j+1].compshape.minx);
						row_rescomponent[i][j+1].compshape.maxy=max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j+1].compshape.maxy);
						row_rescomponent[i][j+1].compshape.miny=min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j+1].compshape.miny);
					/*	row_rescomponent[i][j].compshape.maxx=max(row_rescomponent[i][j].compshape.maxx,row_rescomponent[i][j+1].compshape.maxx);
						row_rescomponent[i][j].compshape.minx=min(row_rescomponent[i][j].compshape.minx,row_rescomponent[i][j+1].compshape.minx);
						row_rescomponent[i][j].compshape.maxy=max(row_rescomponent[i][j].compshape.maxy,row_rescomponent[i][j+1].compshape.maxy);
						row_rescomponent[i][j].compshape.miny=min(row_rescomponent[i][j].compshape.miny,row_rescomponent[i][j+1].compshape.miny);
					*/
					}
				}

			}
		}
		//���´���������ṹ����
		for(j=0;j<row_x[i]-1;j++)
		{
			for(k=j;k<row_x[i];k++)
			{
				if(j!=k&&row_rescomponent[i][j].sign==FALSE&&row_rescomponent[i][k].sign==FALSE)
				{
					if(row_rescomponent[i][j].compshape.maxx-row_rescomponent[i][j].compshape.minx+1+row_rescomponent[i][k].compshape.maxx-row_rescomponent[i][k].compshape.minx+1
						>max(row_rescomponent[i][j].compshape.maxx,row_rescomponent[i][k].compshape.maxx)-min(row_rescomponent[i][j].compshape.minx,row_rescomponent[i][k].compshape.minx)+1)
					{
						row_rescomponent[i][k].sign=TRUE;
						row_rescomponent[i][j].pixelnum=row_rescomponent[i][j].pixelnum+row_rescomponent[i][k].pixelnum;
						row_rescomponent[i][j].compshape.minx=min(row_rescomponent[i][k].compshape.minx,row_rescomponent[i][j].compshape.minx);
						row_rescomponent[i][j].compshape.maxx=max(row_rescomponent[i][k].compshape.maxx,row_rescomponent[i][j].compshape.maxx);
						row_rescomponent[i][j].compshape.miny=min(row_rescomponent[i][k].compshape.miny,row_rescomponent[i][j].compshape.miny);
						row_rescomponent[i][j].compshape.maxy=max(row_rescomponent[i][k].compshape.maxy,row_rescomponent[i][j].compshape.maxy);
					}
				}
			}
		}

	}

}

//��ַ��ʶ��0û��ȷ�����ֻ���POSTCODE_LEN�õ�ȷ�����ֻ�

int Address_Rec(int* proImage, int lWidth,int lHeight, CodeComponent row_rescomponent[Num_line][Num_cha],int *row_x,int row_y, CString *rec_str)
{
	int flag_rec=0;
	int i,j,k;
	int jj,kk;
	character chara_ori[Num_cha];
	character1 chara[Num_cha];
	//ʶ��
	int image_c[200*200];
//	BYTE image_c_gray[160*160];
	int image_n[80*96];
	int wid_c=160;
	int hei_c=160;
	int w_sample=64;
	int h_sample=64;
	int res_n;
	CRect m_charRect;
	CString str1;

//	unsigned char reccc[3];
	int num_code=0;//��ַ���е���Ч�ֻ����룬0��û�ҵ�ȷ���ֻ���1���ҵ��ֻ�����
	//HW
//	BOOL Flag;
//	CFile myfile;
	CString sRead,sRead1;
//	CString sOri,sForChinese;
//	CString idname('x',4);
	//CStdioFile File1;
//	int wid_hw;
//	int hei_hw;
//	int miny_temp;
//	int maxy_temp;
	int temp_hei_n=0;
	int temp_wid_n=0;
	bool post_row=false;
	int CurrentTick=0;
	int postcode[POSTCODE_LEN];//
	char bResult[6];
	char c;
	CRect BCrect;
	for(i=0;i<row_y;i++)
	{
		rec_str[i]="";
		LOGI("row_x[%d]=%d",i,row_x[i]);
		if(row_x[i]<6)//2010-07-28
			continue;

	//	LOGI("3jjjj3");
		k=0;
		LOGI("post_row=%d",post_row);
		if(post_row==false&&row_x[i]<11+1&&row_x[i]>7)//�ж��Ƿ�Ϊ�ֻ���--20100113
		{

			k=0;
			for(j=0;j<row_x[i];j++)
			{
				if(row_rescomponent[i][j].sign==FALSE&&(row_rescomponent[i][j].compshape.maxx-row_rescomponent[i][j].compshape.minx+1)>4&&(double)(row_rescomponent[i][j].compshape.maxx-row_rescomponent[i][j].compshape.minx+1)/
					(row_rescomponent[i][j].compshape.maxy-row_rescomponent[i][j].compshape.miny+1)<1.0)//20170327
				{
					k++;
				}
			}
			if(k>POSTCODE_LEN)
				continue;
			else if(k<POSTCODE_LEN)
			{
				k=0;
				for(j=0;j<row_x[i];j++)
				{
					if((row_rescomponent[i][j].compshape.maxx-row_rescomponent[i][j].compshape.minx+1)<temp_wid&&(row_rescomponent[i][j].compshape.maxx-row_rescomponent[i][j].compshape.minx+1)>8)//20110617��Ӻ�벿��
					{
						k++;
					}
				}
				if(k==POSTCODE_LEN)//20100113
					k=POSTCODE_LEN;
				else
				{
					k=0;
					for(j=0;j<row_x[i];j++)
					{
						if((row_rescomponent[i][j].compshape.maxy-row_rescomponent[i][j].compshape.miny)<temp_hei)
						{
							k++;
						}
					}
					if(k==row_x[i])//����ַ�����ж�--20100416
					{
						for(j=0;j<row_x[i]-1;j++)
						{
							if(row_rescomponent[i][j+1].compshape.minx-row_rescomponent[i][j].compshape.maxx>temp_wid/2)
							{
								k=0;
								break;
							}

						}
					}


					if(k==row_x[i]&&(row_rescomponent[i][row_x[i]-1].compshape.maxx-row_rescomponent[i][0].compshape.minx+1)/11<temp_wid
						&&(row_rescomponent[i][row_x[i]-1].compshape.maxx-row_rescomponent[i][0].compshape.minx+1)/11>temp_wid/2)
					{
						//�ų���5���ַ���ɵ��е�Ӱ��20110617
						k=0;
						for(j=0;j<row_x[i];j++)
						{
							if(row_rescomponent[i][j].compshape.maxx-row_rescomponent[i][j].compshape.minx<max(temp_wid-3,40))
							{
								k++;
							}
						}
						if(k==row_x[i])
							k=0;
						else
							k=POSTCODE_LEN;
					}
				}
			}

			LOGI("k=%d",k);

			if(k==POSTCODE_LEN)//(i==0&&k==POSTCODE_LEN)||(i!=row_y-1&&i!=0&&k==POSTCODE_LEN))//20111019
			{

				post_row=true;
			//	char bResult[6];//����ʶ�𷽷�
			//	int postcode[POSTCODE_LEN];//
				num_code=0;
			//	char c;

				k=0;
				for(j=0;j<row_x[i];j++)
				{
					if(row_rescomponent[i][j].sign==FALSE)
					{
						chara_ori[k].rect_a.left=row_rescomponent[i][j].compshape.minx-2<0?0:row_rescomponent[i][j].compshape.minx-2;
						chara_ori[k].rect_a.right=row_rescomponent[i][j].compshape.maxx+2>lWidth-1?lWidth-1:row_rescomponent[i][j].compshape.maxx+2;
						chara_ori[k].rect_a.top=row_rescomponent[i][j].compshape.miny-2<0?0:row_rescomponent[i][j].compshape.miny-2;
						chara_ori[k].rect_a.bottom=row_rescomponent[i][j].compshape.maxy+2>lHeight-1?lHeight-1:row_rescomponent[i][j].compshape.maxy+2;
						k++;
					}

				}
				row_x[i]=k;

				post_segment(proImage,lWidth,chara_ori,chara,row_x[i]);//�ֻ������·ָ�

				//�ֻ���ʶ��
				for(j=0;j<row_x[i];j++)
				{
					//����ѹ����ֻ��д���-20111013
				/*	if(j==0&&run_num_index>0&&chara[j].rect_c.left-run_num_index<5)
					{
						rec_str[i]+='R';
						rec_strw[i]+='R';
						continue;
					}*/
					for(jj=0;jj<6;jj++)
						bResult[jj]='R';

					memset(image_n,0,80*96*sizeof(int));

					//�Ե��ֵĿ�߽����жϣ����⿽������ͼ��ʱԽ��--2010-07-01
				/*	int ch=chara[j].rect_c.bottom+1-chara[j].rect_c.top;
					int cw=chara[j].rect_c.right+1-chara[j].rect_c.left;
					LOGI("res[%d]=%d-%d",j,ch,cw);*/

				//	LOGI("res[%d]=%d-%d-%d-%d",j,chara[j].rect_c.top,chara[j].rect_c.bottom,chara[j].rect_c.left,chara[j].rect_c.right);
					if(chara[j].rect_c.bottom+1-chara[j].rect_c.top>189||chara[j].rect_c.right+1-chara[j].rect_c.left>189)
						continue;
					if(chara[j].rect_c.bottom+1-chara[j].rect_c.top>85||chara[j].rect_c.right+1-chara[j].rect_c.left>69)
					{
						memset(image_c,255,200*200*sizeof(int));
						for(jj=chara[j].rect_c.top;jj<chara[j].rect_c.bottom+1;jj++)
						{
							for(kk=chara[j].rect_c.left;kk<chara[j].rect_c.right+1;kk++)
							{
								image_c[(jj-chara[j].rect_c.top)*200+kk-chara[j].rect_c.left]
									=proImage[jj*lWidth+kk];
							}
						}
						BCrect.left=0;
						BCrect.right=chara[j].rect_c.right-chara[j].rect_c.left;
						BCrect.top=0;
						BCrect.bottom=chara[j].rect_c.bottom-chara[j].rect_c.top;
						StdDIBbyRect(image_c, BCrect, 200, 200, 16, 32);
						for(jj=BCrect.top;jj<BCrect.bottom+1;jj++)
						{
							for(kk=BCrect.left;kk<BCrect.right+1;kk++)
							{
								image_n[(jj-BCrect.top+10)*80+kk-BCrect.left+10]
									=255-image_c[jj*200+kk];
							}
						}
					}
					else
					{
						for(jj=chara[j].rect_c.top;jj<chara[j].rect_c.bottom+1;jj++)
						{
							for(kk=chara[j].rect_c.left;kk<chara[j].rect_c.right+1;kk++)
							{
								image_n[(jj-chara[j].rect_c.top+10)*80+kk-chara[j].rect_c.left+10]
								        =255-proImage[jj*lWidth+kk];
							}
						}
					}

//					try{
				//	LOGI("j=%d",j);
				//	LOGI("2223");
				//	res_n=-1;
					res_n=recnum_jing(image_n,80,96);
				//	if(j==0)
				//		res_n=1;
					LOGI("res_n[%d]=%d",j,res_n);
//					}
//					catch(...)
//					{
//						flag_rec= -21;
//					}
//					LOGI("222222223333");
					if(res_n>9)
						c='R';
					else
						c=res_n+48;
					bResult[0] = c;

//					try{
						res_n=recnum_jingsvm(image_n,80,96);
						LOGI("svm  ---------res_n[%d]=%d",j,res_n);
//					}
//					catch(...)
//					{
//						flag_rec=-22;
//					}
					if(res_n>9)
						c='R';
					else
						c=res_n+48;
					bResult[1] =  c;


					//********************20110823******************************
					c=vote_win(bResult,2);

					//�ۺ϶���ʶ����
					if(bResult[0]==bResult[1])
					{
						//CString bR=;
						rec_str[i]+=bResult[0];

					}
					else
						rec_str[i]+="R";
					LOGI("rec_str[%d]=%s",i,rec_str[i].GetString());


					//****************end of 20110823***************************
				/*	if(c!='R')
					{
						num_code++;
						postcode[j]=c;
					}*/
					postcode[j]=c;
					num_code=POSTCODE_LEN;

				}
				if(num_code==POSTCODE_LEN)
				{
					for(j=0;j<POSTCODE_LEN;j++)
					{
						Win_Postcode[j]=postcode[j];
					}
					break;
				}
				else
					num_code=0;

				continue;
			}
		}

		//�ж��ַ�����
		int chi_n=0;
		temp_hei_n=0;
		k=0;

		for(j=0;j<row_x[i];j++)
		{
			//�������ֻ�
			if(post_row==false&&(row_x[i]>10&&(j+11)<row_x[i]+1))//20110617-6to5
			{
				post_row=Comp_Post(row_rescomponent, i, j, row_x, row_y);
				if(post_row==true)//�ҵ��ֻ�
				{

					num_code=0;

					for(jj=j;jj<j+11;jj++)
					{
						chara[k].type_c=3;//20100125--1 to 3
						chara[k].rect_c.left=row_rescomponent[i][jj].compshape.minx-2<0?0:row_rescomponent[i][jj].compshape.minx-2;
						chara[k].rect_c.right=row_rescomponent[i][jj].compshape.maxx+2>lWidth-1?lWidth-1:row_rescomponent[i][jj].compshape.maxx+2;
						chara[k].rect_c.top=row_rescomponent[i][jj].compshape.miny-2<0?0:row_rescomponent[i][jj].compshape.miny-2;
						chara[k].rect_c.bottom=row_rescomponent[i][jj].compshape.maxy+2>lHeight-1?lHeight-1:row_rescomponent[i][jj].compshape.maxy+2;
						k++;
					}
					j=j+11;
					//�ֻ�ʶ��
					for(int m=k-11;m<k;m++)
					{
							//����ѹ����ֻ��д���-20111013
					/*	if((m==0&&run_num_index>0&&chara[m].rect_c.left-run_num_index<5)||(i!=0&&i==row_y-1))//20111019--���i!=0
						{
							break;
						}*/
						for(jj=0;jj<6;jj++)//����ʶ���㷨
							bResult[jj]='R';

						memset(image_n,0,80*96*sizeof(int));

						//�Ե��ֵĿ�߽����жϣ����⿽������ͼ��ʱԽ��--2010-07-01
						if(chara[m].rect_c.bottom+1-chara[m].rect_c.top>189||chara[m].rect_c.right+1-chara[m].rect_c.left>189)
							continue;
						if(chara[m].rect_c.bottom+1-chara[m].rect_c.top>85||chara[m].rect_c.right+1-chara[m].rect_c.left>69)
						{
							memset(image_c,255,200*200*sizeof(int));
							for(jj=chara[m].rect_c.top;jj<chara[m].rect_c.bottom+1;jj++)
							{
								for(kk=chara[m].rect_c.left;kk<chara[m].rect_c.right+1;kk++)
								{
									image_c[(jj-chara[m].rect_c.top)*200+kk-chara[m].rect_c.left]
										=proImage[jj*lWidth+kk];
								}
							}
							BCrect.left=0;
							BCrect.right=chara[m].rect_c.right-chara[m].rect_c.left;
							BCrect.top=0;
							BCrect.bottom=chara[m].rect_c.bottom-chara[m].rect_c.top;
							StdDIBbyRect(image_c, BCrect, 200, 200, 16, 32);
							for(jj=BCrect.top;jj<BCrect.bottom+1;jj++)
							{
								for(kk=BCrect.left;kk<BCrect.right+1;kk++)
								{
									image_n[(jj-BCrect.top+10)*80+kk-BCrect.left+10]
										=255-image_c[jj*200+kk];
								}
							}
						}
						else
						{
							for(jj=chara[m].rect_c.top;jj<chara[m].rect_c.bottom+1;jj++)
							{
								for(kk=chara[m].rect_c.left;kk<chara[m].rect_c.right+1;kk++)
								{
									image_n[(jj-chara[m].rect_c.top+10)*80+kk-chara[m].rect_c.left+10]
											      =255-proImage[jj*lWidth+kk];
								}
							}
						}


//						try{
							res_n=recnum_jing(image_n,80,96);
							LOGI("hangnei-res_n[%d]=%d",m-k+11,res_n);
//						}
//						catch(...)
//						{
//							flag_rec=-21;
//						}
						if(res_n>9)
							c='R';
						else
							c=res_n+48;
						//	itoa(res_n,&c,10);
						bResult[0] =  c;
//						try{
							res_n=recnum_jingsvm(image_n,80,96);
							LOGI("hangnei-svm-res_n[%d]=%d",m-k+11,res_n);
//						}
//						catch(...)
//						{
//							flag_rec=-22;
//						}
						if(res_n>9)
							c='R';
						else
							c=res_n+48;
						//	itoa(res_n,&c,10);
						bResult[1] =  c;


						//�ۺ϶���ʶ����
						c=vote_win(bResult,2);
						/*if(c!='R')
						{
							num_code++;
							postcode[m-k+11]=c;
						}*/
						postcode[m-k+11]=c;
						num_code=POSTCODE_LEN;
					}
					LOGI("num_code=%d",num_code);
					if(num_code==POSTCODE_LEN)
					{
						for(jj=0;jj<POSTCODE_LEN;jj++)
						{
							Win_Postcode[jj]=postcode[jj];
						}
						break;
					}
					else
						num_code=0;

				}
			}
			if(row_rescomponent[i][j].sign==FALSE&&j<row_x[i])
			{
				chara[k].rect_c.left=row_rescomponent[i][j].compshape.minx-2<0?0:row_rescomponent[i][j].compshape.minx-2;
				chara[k].rect_c.right=row_rescomponent[i][j].compshape.maxx+2>lWidth-1?lWidth-1:row_rescomponent[i][j].compshape.maxx+2;
				chara[k].rect_c.top=row_rescomponent[i][j].compshape.miny-2<0?0:row_rescomponent[i][j].compshape.miny-2;
				chara[k].rect_c.bottom=row_rescomponent[i][j].compshape.maxy+2>lHeight-1?lHeight-1:row_rescomponent[i][j].compshape.maxy+2;
				//�ų����ţ���ŵȱ����ż�����
				if(row_rescomponent[i][j].compshape.maxy-row_rescomponent[i][j].compshape.miny<temp_hei/2&&
					row_rescomponent[i][j].compshape.maxx-row_rescomponent[i][j].compshape.minx<2*(row_rescomponent[i][j].compshape.maxy-row_rescomponent[i][j].compshape.miny))
					chara[k].type_c=2;
				//�ų�ð��
				else if(row_rescomponent[i][j].compshape.maxy-row_rescomponent[i][j].compshape.miny>temp_hei/2-1&&(row_rescomponent[i][j].compshape.maxx-row_rescomponent[i][j].compshape.minx+1)*
					(row_rescomponent[i][j].compshape.maxy-row_rescomponent[i][j].compshape.miny+1)*7<temp_hei*temp_wid)
					chara[k].type_c=2;

				else if((row_rescomponent[i][j].compshape.maxx-row_rescomponent[i][j].compshape.minx>temp_wid*5/4))
				//	&&row_rescomponent[i][j].compshape.maxy-row_rescomponent[i][j].compshape.miny>temp_hei/2-1))
				{	chara[k].type_c=0;//����shujing20161111
					temp_hei_n=temp_hei_n+(chara[k].rect_c.bottom-chara[k].rect_c.top+1);//ͳ�ƺ��ֵ�ƽ���߶�
					chi_n++;
				}
				else
				{
					chara[k].type_c=1;//���ֻ���ĸ

				}
				k++;
			}
		}
		if(num_code==POSTCODE_LEN)
			break;

		LOGI("no phone-%d",i);
	//	if(row_x[i]<11)
	//		break;
		row_x[i]=k;
		if(chi_n>0)
		{
			temp_hei_n=temp_hei_n/chi_n;
		/*	for(j=0;j<row_x[i];j++)//���Ƚ�խ�ĺ��֣��硰Ŀ��
			{
				if(chara[j].type_c==1&&abs(chara[j].rect_c.bottom-chara[j].rect_c.top+1-temp_hei_n)<temp_hei_n/12)
					chara[j].type_c=0;
			}*/
		}
		//�ų�ճ������
		int AB=0;
		for(j=1;j<row_x[i];j++)
		{
			if(chara[j].type_c==0&&(chara[j].rect_c.bottom-chara[j].rect_c.top+1)<temp_hei_n+3)
			{
				if((chara[j-1].type_c==1&&abs(chara[j].rect_c.bottom-chara[j].rect_c.top-chara[j-1].rect_c.bottom+chara[j-1].rect_c.top)<2)//20170327
					||(chara[j+1].type_c==1&&abs(chara[j].rect_c.bottom-chara[j].rect_c.top-chara[j+1].rect_c.bottom+chara[j+1].rect_c.top)<2))
				{
					//�ָ�ճ������
					if(chara[j-1].type_c==1)
					{
						AB=-1;
						if(AB==-1&&chara[j].rect_c.left-chara[j-1].rect_c.right<8)//20170327 �����ҷֿ�����
							segment_num(proImage,lWidth,chara, j, row_x[i], AB);
						else if(chara[j+1].type_c==1)
						{
							AB=1;
							if(AB==1&&chara[j+1].rect_c.left-chara[j].rect_c.right<8)
								segment_num(proImage,lWidth,chara, j, row_x[i], AB);
						}
					}
				}
			}
		}
		LOGI("row_x[%d]-%d",i,row_x[i]);
		//�����������ʱ�
		for(j=0;j<row_x[i];j++)
		{
			//�������ֻ�
			if(post_row==false&&(row_x[i]>10&&(j+11)<row_x[i]+1))//20110617-6to5
			{
				post_row = Comp_Phone(chara, j);
				LOGI("post_row-%d",post_row);
				if(post_row==true)
				{
					//ʶ��
					num_code=0;
					for(int m=j;m<j+11;m++)
					{
						for(jj=0;jj<6;jj++)//����ʶ���㷨
							bResult[jj]='R';

						memset(image_n,0,80*96*sizeof(int));

						//�Ե��ֵĿ�߽����жϣ����⿽������ͼ��ʱԽ��--2010-07-01
						if(chara[m].rect_c.bottom+1-chara[m].rect_c.top>189||chara[m].rect_c.right+1-chara[m].rect_c.left>189)
							continue;
						if(chara[m].rect_c.bottom+1-chara[m].rect_c.top>85||chara[m].rect_c.right+1-chara[m].rect_c.left>69)
						{
							memset(image_c,255,200*200*sizeof(int));
							for(jj=chara[m].rect_c.top;jj<chara[m].rect_c.bottom+1;jj++)
							{
								for(kk=chara[m].rect_c.left;kk<chara[m].rect_c.right+1;kk++)
								{
									image_c[(jj-chara[m].rect_c.top)*200+kk-chara[m].rect_c.left]
										=proImage[jj*lWidth+kk];
								}
							}
							BCrect.left=0;
							BCrect.right=chara[m].rect_c.right-chara[m].rect_c.left;
							BCrect.top=0;
							BCrect.bottom=chara[m].rect_c.bottom-chara[m].rect_c.top;
							StdDIBbyRect(image_c, BCrect, 200, 200, 16, 32);
							for(jj=BCrect.top;jj<BCrect.bottom+1;jj++)
							{
								for(kk=BCrect.left;kk<BCrect.right+1;kk++)
								{
									image_n[(jj-BCrect.top+10)*80+kk-BCrect.left+10]
										=255-image_c[jj*200+kk];
								}
							}
						}
						else
						{
							for(jj=chara[m].rect_c.top;jj<chara[m].rect_c.bottom+1;jj++)
							{
								for(kk=chara[m].rect_c.left;kk<chara[m].rect_c.right+1;kk++)
								{
									image_n[(jj-chara[m].rect_c.top+10)*80+kk-chara[m].rect_c.left+10]
											      =255-proImage[jj*lWidth+kk];
								}
							}
						}



						res_n=recnum_jing(image_n,80,96);
						LOGI("hangnei1-res_n[%d]=%d",m-k+11,res_n);

						if(res_n>9)
							c='R';
						else
							c=res_n+48;
						bResult[0] =  c;
					//						try{
						res_n=recnum_jingsvm(image_n,80,96);
						LOGI("hangnei1-svm-res_n[%d]=%d",m-k+11,res_n);

						if(res_n>9)
							c='R';
						else
							c=res_n+48;

						bResult[1] =  c;


						//�ۺ϶���ʶ����
						c=vote_win(bResult,2);
											/*if(c!='R')
											{
												num_code++;
												postcode[m-k+11]=c;
											}*/
						postcode[m-j]=c;
						num_code=POSTCODE_LEN;
					}
					LOGI("num_code=%d",num_code);
					if(num_code==POSTCODE_LEN)
					{
						for(jj=0;jj<POSTCODE_LEN;jj++)
						{
							Win_Postcode[jj]=postcode[jj];
						}
						break;
					}
					else
						num_code=0;


				}

			}
		}
		if(num_code==POSTCODE_LEN)
			break;

//		CurrentTick=GetTickCount();
	/*	if(opti==2&&CurrentTick-BeginTick>TotalTime*9/10)
		{
			rec_str[i]= rec_strh[i];
			rec_strw[i]=rec_strh[i];
			continue;
		}*/

		CString str11;
		str11="";

		int nnn=0;
		for(j=0;j<row_x[i];j++)
		{
		//	nnn=0;
			//�ų��ϳ�������ĸ��
			//����ʶ��
			LOGI("chara[%d].type_c=%d",j,chara[j].type_c);
			if(chara[j].type_c==1||chara[j].type_c==3)//���ֻ���ĸʶ��--20100125
			{
				nnn++;
				memset(image_n,0,80*96*sizeof(int));
				LOGI("temp_hei=%d-%d",chara[j].rect_c.bottom-chara[j].rect_c.top+1,temp_hei);
				//�Ե��ֵĿ�߽����жϣ����⿽������ͼ��ʱԽ��--2010-07-01
				if(chara[j].rect_c.bottom+1-chara[j].rect_c.top>189||chara[j].rect_c.right+1-chara[j].rect_c.left>189)
					continue;
				if(chara[j].rect_c.bottom+1-chara[j].rect_c.top>85||chara[j].rect_c.right+1-chara[j].rect_c.left>69)
				{
					memset(image_c,255,200*200*sizeof(int));
					for(jj=chara[j].rect_c.top;jj<chara[j].rect_c.bottom+1;jj++)
					{
						for(kk=chara[j].rect_c.left;kk<chara[j].rect_c.right+1;kk++)
						{
							image_c[(jj-chara[j].rect_c.top)*200+kk-chara[j].rect_c.left]
								=proImage[jj*lWidth+kk];
						}
					}
					BCrect.left=0;
					BCrect.right=chara[j].rect_c.right-chara[j].rect_c.left;
					BCrect.top=0;
					BCrect.bottom=chara[j].rect_c.bottom-chara[j].rect_c.top;
					StdDIBbyRect(image_c, BCrect, 200, 200, 16, 32);
					for(jj=BCrect.top;jj<BCrect.bottom+1;jj++)
					{
						for(kk=BCrect.left;kk<BCrect.right+1;kk++)
						{
							image_n[(jj-BCrect.top+10)*80+kk-BCrect.left+10]
								=255-image_c[jj*200+kk];
						}
					}
				}
				else
				{
					for(jj=chara[j].rect_c.top;jj<chara[j].rect_c.bottom+1;jj++)
					{
						for(kk=chara[j].rect_c.left;kk<chara[j].rect_c.right+1;kk++)
						{
							image_n[(jj-chara[j].rect_c.top+10)*80+kk-chara[j].rect_c.left+10]
							        =255-proImage[jj*lWidth+kk];
						}
					}
				}
				//�����ַ���-���Ĵ���
				LOGI("temp_hei=%d-%d",chara[j].rect_c.bottom-chara[j].rect_c.top+1,temp_hei);
				if(chara[j].rect_c.bottom-chara[j].rect_c.top+1<temp_hei/3)//shujing20161111
				{

					continue;

				}
				else
				{
				/*	#ifdef DIR_ADDREC
					char path[100];
					sprintf(path,"E:\\MPS\\%d_i.jpg",j+i*30);
					CImgFile *dFile = IfxCreateImgFile(path);
					if(dFile!=NULL)
						dFile->WriteFile(image_n,80,96,8);
					delete dFile;
					#endif*/
				//	DWORD totaltime;
				//	totaltime=GetTickCount();
//					try{

						res_n=recnum_jing(image_n,80,96);
//					}
//					catch(...)
//					{
//						flag_rec= -22;
//					}
				//	totaltime=GetTickCount()-totaltime;
					if(res_n<10&&res_n>-1)
					{

						//str1.Format("%d",res_n);
						c=res_n+48;
						str1="";
						str1 += c;

					}

					else
					{
						str1="R";
					}
//
				}

				str11 += str1;


			}
			/*else
			{
				LOGI("num_code=%d",num_code);
				rec_str[i]+='';
				LOGI("1 num_code=%d",num_code);
				rec_strw[i]+="";
				LOGI("2 num_code=%d",num_code);
			}*/

		}
		if(nnn>0)
			rec_str[i]=str11;


	}
//	LOGI("flag_rec=%d",flag_rec);
	if(flag_rec<0)
		num_code=flag_rec;
	LOGI("num_code=%d",num_code);
	return num_code;

}
//�����зָ�(2009-08-12)
void row_segment1(CodeComponent *rescomponent, int Comp_num,CodeComponent row_rescomponent[Num_line][Num_cha],int &row_y, int *row_x,int num_r)
{
	int i,j;
	int row_n=0;
	int minx[Num_line][Num_cha];
	int row_x1[Num_line];
	int top[Num_line];
	int bottom[Num_line];
	for(i=0;i<Num_line;i++)
	{
		row_x1[i]=0;
		top[i]=1000;
		bottom[i]=0;
	}
	for(i=0;i<Comp_num;i++)
	{
		if((rescomponent[i].compshape.maxx-rescomponent[i].compshape.minx+1>temp_wid/2&&rescomponent[i].compshape.maxx-rescomponent[i].compshape.minx+1<3*temp_wid/2)||
			(rescomponent[i].compshape.maxy-rescomponent[i].compshape.miny+1>temp_hei/2&&rescomponent[i].compshape.maxy-rescomponent[i].compshape.miny+1<3*temp_hei/2))
		{
			minx[0][0]=i;
			row_x1[0]++;

			if(rescomponent[i].compshape.miny<top[row_n])
				top[row_n]=rescomponent[i].compshape.miny;
			if(rescomponent[i].compshape.maxy>bottom[row_n])
				bottom[row_n]=rescomponent[i].compshape.maxy;
			row_n++;
			break;
		}
	}
	if(minx[0][0]<0)
		return;
	int min_dis=0;
	int min_index=0;//��ӽ����б�
	//����������ͨԪλ�ù�ϵ�зָ�
	for(i=minx[0][0]+1;i<Comp_num;i++)
	{
		min_dis=1000;
		min_index=0;
		for(j=0;j<row_n;j++)//20090922���
		{
			if(abs((rescomponent[i].compshape.maxy+rescomponent[i].compshape.miny)/2-(rescomponent[minx[j][row_x1[j]-1]].compshape.maxy+rescomponent[minx[j][row_x1[j]-1]].compshape.miny)/2)<min_dis)
			{
				min_dis=abs((rescomponent[i].compshape.maxy+rescomponent[i].compshape.miny)/2-(rescomponent[minx[j][row_x1[j]-1]].compshape.maxy+rescomponent[minx[j][row_x1[j]-1]].compshape.miny)/2);
				min_index=j;
			}
		}
		//20091118
		if((row_x1[min_index]==1&&max(rescomponent[i].compshape.maxy,rescomponent[minx[min_index][row_x1[min_index]-1]].compshape.maxy)-min(rescomponent[i].compshape.miny,rescomponent[minx[min_index][row_x1[min_index]-1]].compshape.miny)<temp_hei*6/5)
			||abs((rescomponent[i].compshape.maxy+rescomponent[i].compshape.miny)/2-(rescomponent[minx[min_index][row_x1[min_index]-1]].compshape.maxy+rescomponent[minx[min_index][row_x1[min_index]-1]].compshape.miny)/2)<temp_hei/2
			||(row_x1[min_index]-1>0&&abs((rescomponent[i].compshape.maxy+rescomponent[i].compshape.miny)/2-(rescomponent[minx[min_index][row_x1[min_index]-2]].compshape.maxy+rescomponent[minx[min_index][row_x1[min_index]-2]].compshape.miny)/2)<temp_hei/2))
		{
			minx[min_index][row_x1[min_index]]=i;
			row_x1[min_index]++;
			if(rescomponent[i].compshape.miny<top[min_index])
				top[min_index]=rescomponent[i].compshape.miny;
			if(rescomponent[i].compshape.maxy>bottom[min_index])
				bottom[min_index]=rescomponent[i].compshape.maxy;
			i++;
			while(abs((rescomponent[i].compshape.maxy+rescomponent[i].compshape.miny)/2-(rescomponent[minx[min_index][row_x1[min_index]-1]].compshape.maxy+rescomponent[minx[min_index][row_x1[min_index]-1]].compshape.miny)/2)<temp_hei/2
				||(row_x1[min_index]-1>0&&abs((rescomponent[i].compshape.maxy+rescomponent[i].compshape.miny)/2-(rescomponent[minx[min_index][row_x1[min_index]-2]].compshape.maxy+rescomponent[minx[min_index][row_x1[min_index]-2]].compshape.miny)/2)<temp_hei/2))
			{
				minx[min_index][row_x1[min_index]]=i;
				row_x1[min_index]++;
				if(rescomponent[i].compshape.miny<top[min_index])
					top[min_index]=rescomponent[i].compshape.miny;
				if(rescomponent[i].compshape.maxy>bottom[min_index])
					bottom[min_index]=rescomponent[i].compshape.maxy;
				i++;
				if(row_x1[min_index]>Num_cha)
					break;
			}
			i--;
		}
		else
		{
			minx[row_n][0]=i;
			row_x1[row_n]++;
			if(rescomponent[i].compshape.miny<top[row_n])
				top[row_n]=rescomponent[i].compshape.miny;
			if(rescomponent[i].compshape.maxy>bottom[row_n])
				bottom[row_n]=rescomponent[i].compshape.maxy;
			row_n++;
		}
		if(row_n+row_y>Num_line-1)//20090922
		{
			row_n=Num_line-1-row_y;//20100107
			break;
		}
	}
	//���Ƿ��п��Ժϲ�����
	int k,kk;
	kk=0;
	if(row_n>num_r)
	{
		for(i=0;i<row_n;i++)
		{
			for(j=i+1;j<row_n;j++)
			{
				if(top[i]!=-1&&top[j]!=-1)
				{
					if(max(bottom[i],bottom[j])-min(top[i],top[j])<temp_hei*3/2&&min(bottom[i],bottom[j])-max(top[i],top[j])>temp_hei*2/3)
					{
						top[i]=min(top[i],top[j]);
						bottom[i]=max(bottom[i],bottom[j]);
						for(k=0;k<row_x1[j];k++)
						{
							minx[i][row_x1[i]]=minx[j][k];
							row_x1[i]++;
						}
						top[j]=-1;
						kk++;
					}
				}
			}
		}

	}
	double *comp_miny;
	int *sn;
	comp_miny=(double*)malloc(row_n*sizeof(double));
	if(comp_miny==NULL)
		return;
	sn=(int*)malloc(row_n*sizeof(int));
	if(sn==NULL)
	{
		free(comp_miny);
		return;
	}
	for(j=0;j<row_n;j++)
	{
		if(top[j]!=-1)
			comp_miny[j]=(double)rescomponent[minx[j][0]].compshape.miny;
		else
			comp_miny[j]=10000;

		sn[j]=j;
	}
	Quick_Order(comp_miny,0,row_n-1,sn);
	row_n=row_n-kk;
	for(i=0;i<row_n;i++)
	{

		if(row_x1[sn[i]]>Num_cha-1)
			row_x1[sn[i]]=Num_cha-1;
		for(j=0;j<row_x1[sn[i]];j++)
		{
			row_rescomponent[i+row_y][j].sign=FALSE;
			row_rescomponent[i+row_y][j].pixelnum=rescomponent[minx[sn[i]][j]].pixelnum;
			row_rescomponent[i+row_y][j].compshape.minx=rescomponent[minx[sn[i]][j]].compshape.minx;
			row_rescomponent[i+row_y][j].compshape.maxx=rescomponent[minx[sn[i]][j]].compshape.maxx;
			row_rescomponent[i+row_y][j].compshape.miny=rescomponent[minx[sn[i]][j]].compshape.miny;
			row_rescomponent[i+row_y][j].compshape.maxy=rescomponent[minx[sn[i]][j]].compshape.maxy;
		}
		Comp_Order(row_rescomponent[i+row_y], row_x1[sn[i]]);
		row_x[i+row_y]=row_x1[sn[i]];
	}
	free(comp_miny);
	free(sn);
	row_y=row_y+row_n;

}

//�ָ�ճ������
void segment_num(int* image, int lWidth,character1* chara_new, int num_index, int &num_c,int AB)
{
	int i;
	int k=1;
	if(chara_new[num_index].rect_c.right-chara_new[num_index].rect_c.left+1<(chara_new[num_index+AB].rect_c.right-chara_new[num_index+AB].rect_c.left+1)*2.2||
		(chara_new[num_index].rect_c.right-chara_new[num_index].rect_c.left+1)<(chara_new[num_index].rect_c.bottom-chara_new[num_index].rect_c.top+1)*1.3)
		k=2;
	else if(chara_new[num_index].rect_c.right-chara_new[num_index].rect_c.left+1<(chara_new[num_index+AB].rect_c.right-chara_new[num_index+AB].rect_c.left+1)*3.2||
		(chara_new[num_index].rect_c.right-chara_new[num_index].rect_c.left+1)<(chara_new[num_index].rect_c.bottom-chara_new[num_index].rect_c.top+1)*1.9)
		k=3;
	else
		k=4;
	if(k==1)
		return;
	int left,right,top,bottom;
	left=chara_new[num_index].rect_c.left;
	right=chara_new[num_index].rect_c.right;
	top=chara_new[num_index].rect_c.top;
	bottom=chara_new[num_index].rect_c.bottom;
	num_c=num_c+k-1;
	for(i=num_c-1;i>num_index+k-1;i--)
	{
		chara_new[i].rect_c.left=chara_new[i-k+1].rect_c.left;
		chara_new[i].rect_c.right=chara_new[i-k+1].rect_c.right;
		chara_new[i].rect_c.top=chara_new[i-k+1].rect_c.top;
		chara_new[i].rect_c.bottom=chara_new[i-k+1].rect_c.bottom;
		chara_new[i].type_c=chara_new[i-k+1].type_c;
	}
	for(i=num_index;i<num_index+k;i++)
	{
		chara_new[i].rect_c.left=left+(i-num_index)*((right-left)/k);
		chara_new[i].rect_c.right=left+(i-num_index+1)*((right-left)/k);
		chara_new[i].rect_c.top=top;
		chara_new[i].rect_c.bottom=bottom;
		chara_new[i].type_c=1;
	}

}
//�����б�
int FontRecognition(CodeComponent *rescomponent, int digi)//20091117
{
	int i;

	int *hei_comp;
	hei_comp=(int*)malloc(digi*sizeof(int));

	memset(hei_comp,0, digi*sizeof(int));

	int num_h=0;
	int x_last=0;
	for(i=0;i<digi;i++)
	{
		if(rescomponent[i].sign==FALSE)
		{
			if(rescomponent[i].compshape.maxy-rescomponent[i].compshape.miny+1>temp_hei*1/2
				&&(rescomponent[i].compshape.maxy-rescomponent[i].compshape.miny+1<temp_hei*6/5||
				rescomponent[i].compshape.maxy-rescomponent[i].compshape.miny+1<temp_wid)	//add--20100114
				&&rescomponent[i].compshape.maxx-rescomponent[i].compshape.minx+1>temp_wid*1/3)//�ų�С�����ŵĸ���
			{
				if(num_h==0)
				{
					hei_comp[num_h]=rescomponent[i].compshape.maxy-rescomponent[i].compshape.miny+1;
					num_h++;
					x_last=rescomponent[i].compshape.maxx;
				}
				else if(rescomponent[i].compshape.minx-x_last<3*temp_wid)
				{
					hei_comp[num_h]=rescomponent[i].compshape.maxy-rescomponent[i].compshape.miny+1;
					num_h++;
					x_last=rescomponent[i].compshape.maxx;
				}
				else
				{
					if(num_h>4)
						break;
					num_h=0;
					hei_comp[num_h]=rescomponent[i].compshape.maxy-rescomponent[i].compshape.miny+1;
					num_h++;
					x_last=rescomponent[i].compshape.maxx;
				}

			}

		}
	}
	if(num_h<5)
	{
		free(hei_comp);
		return 2;
	}

	//����ͨԪ��ߵľ�ֵ

	int hei_mean=0;

	for(i=0;i<num_h;i++)
		hei_mean+=hei_comp[i];
	if(num_h!=0)
	hei_mean=hei_mean/num_h;
	//����ͨԪ��ߵķ���

	int hei_dev=0;

	for(i=0;i<num_h;i++)
	{
		hei_dev+=(hei_comp[i]-hei_mean)*(hei_comp[i]-hei_mean);
	}
	if(num_h!=0)
		hei_dev=hei_dev/num_h;
	hei_dev=(int)sqrt(hei_dev);
	free(hei_comp);

	if(hei_dev <temp_hei/8+1)
		return 0;
	else
	{
		int cluster=0;
		cluster=FontRecognition_Cluster(rescomponent, digi);
		return cluster;
	}
}

//�����б�(����)
int FontRecognition_Cluster(CodeComponent *rescomponent, int digi)//20091119
{

	Pointf* ptsh;
	ptsh=(Pointf*) malloc(digi*sizeof(Pointf));
	PointZ* ZArray;
	ZArray=(PointZ*) malloc(digi*sizeof(PointZ));
	int *Nj;
	Nj=(int*)malloc(digi*sizeof(int));
	int i;
	int num_h=0;
	int x_last=0;
	for(i=0;i<digi;i++)
	{
		if(rescomponent[i].sign==FALSE)
		{

			if(rescomponent[i].compshape.maxy-rescomponent[i].compshape.miny+1>temp_hei*1/2
				&&rescomponent[i].compshape.maxy-rescomponent[i].compshape.miny+1<temp_hei*6/5
				&&rescomponent[i].compshape.maxx-rescomponent[i].compshape.minx+1>temp_wid*1/3)//�ų�С�����ŵĸ���
			{
				if(num_h==0)
				{
					ptsh[num_h].x=(float)rescomponent[i].compshape.maxy-rescomponent[i].compshape.miny+1;
					ptsh[num_h].y=0;
					ptsh[num_h].sequence=num_h;
					num_h++;
					x_last=rescomponent[i].compshape.maxx;
				}
				else if(rescomponent[i].compshape.minx-x_last<3*temp_wid)
				{
					ptsh[num_h].x=(float)rescomponent[i].compshape.maxy-rescomponent[i].compshape.miny+1;
					ptsh[num_h].y=0;
					ptsh[num_h].sequence=num_h;
					num_h++;
					x_last=rescomponent[i].compshape.maxx;
				}
				else
				{
					if(num_h>4)
						break;
					num_h=0;
					ptsh[num_h].x=(float)rescomponent[i].compshape.maxy-rescomponent[i].compshape.miny+1;
					ptsh[num_h].y=0;
					ptsh[num_h].sequence=num_h;
					num_h++;
					x_last=rescomponent[i].compshape.maxx;
				}

			}

		}
	}
	if(num_h<5)
	{
		free(ptsh);
		free(ZArray);
		free(Nj);
		return 2;
	}
	int Nc=num_h;

	for(i=0;i<Nc;i++)
	{
		ZArray[i].x=ptsh[i].x;
		ZArray[i].y=ptsh[i].y;
	}

	float ThetaC=7;
	int L=1;
	int I=100;

	Nc=isodata(num_h, ptsh, Nc, ZArray, Nj, ThetaC,L, I);


	if(Nc<3)
	{
		free(ptsh);
		free(ZArray);
		free(Nj);
		return 0;
	}
	else if(Nc==3)
	{
		for(i=0;i<Nc;i++)
			if(Nj[i]>num_h*2/3)
				break;
		if(i<Nc)
		{
			free(ptsh);
			free(ZArray);
			free(Nj);
			return 0;
		}
		else
		{
			free(ptsh);
			free(ZArray);
			free(Nj);
			return 1;
		}
	}
	else
	{
		free(ptsh);
		free(ZArray);
		free(Nj);
		return 1;
	}
}


//��ַ�����ֵ��
BOOL  ThresholdOtsu_add(int * proImage, int lWidth, int lHeight,int backcolor, RECT barRect)
{
	int Temp;
	int thresholdvalue=1;
	int i,j;
	//int lLineBytes;
    //lLineBytes = WIDTHBYTES(lWidth * 8);
//	if(barRect.left==-1)
		thresholdvalue=Otsu (proImage, lWidth, lHeight, 0, 0, lWidth, lHeight);

//	else
//		thresholdvalue=Otsu_bar (proImage, lWidth, lHeight, 0, 0, lWidth, lHeight, barRect);

	if(backcolor!=0)//20100120;
	{
		if(backcolor-thresholdvalue<20&&backcolor>50)
			thresholdvalue=backcolor-20;
		if(backcolor-thresholdvalue>40)
			thresholdvalue=backcolor-40;
	}
	//ͳ��ǰ�����ر���
	int pp=0;
	for(i=0; i<lHeight; i++)
	{
		for(j=0; j<lWidth; j++)
		{
			Temp =proImage[(lHeight-i-1)*lWidth+j];
			if(Temp<thresholdvalue)
				pp++;
		}
	}
	if((double)pp/(lHeight*lWidth)<0.09)
		thresholdvalue+=20;


	for(i=0; i<lHeight; i++)
	{
		for(j=0; j<lWidth; j++)
		{
			Temp =proImage[(lHeight-i-1)*lWidth+j];
			if(Temp<thresholdvalue)
			{
				proImage[(lHeight-i-1)*lWidth+j]=0;
			}
			else
			{
				proImage[(lHeight-i-1)*lWidth+j]=255;
			}
		}
	}
	return TRUE;
}


void Zoom_IAddress(int* image1,int* image2, int &width, int &height, float scale)
{
	int i,j;
	int* image_t1;
	int* image_t2;
	int width_n=0;
	int height_n=0;
	width_n=(int)(width/scale);
	height_n=(int)(height/scale);
    image_t2=(int*)malloc(width_n*height_n*sizeof(int));
    image_t1=(int*)malloc(width_n*height_n*sizeof(int));
	int ii,jj;
	for(i=0;i<height_n;i++)
	{
		ii=(int)(i*scale);
		for(j=0;j<width_n;j++)
		{
			jj=(int)(j*scale);
			image_t1[i*width_n+j]=image1[ii*width+jj];
			image_t2[i*width_n+j]=image2[ii*width+jj];//error,out of memory 0421
		}
	}
	memcpy(image1,image_t1,width_n*height_n*sizeof(int));
	memcpy(image2,image_t2,width_n*height_n*sizeof(int));


	width=width_n;
	height=height_n;
	free(image_t1);
	image_t1=NULL;
	free(image_t2);
	image_t2=NULL;

}
//ɾ���ǵ�ַ��
void row_recognition(int lWidth,int lHeight, CodeComponent row_rescomponent[Num_line][Num_cha],int *row_x,int row_y)
{
	int i,j,k;
	int jj,kk;
	for(i=0;i<row_y;i++)
	{
		//ȥ���ַ��������ĵ�ַ��
		k=0;
		for(j=0;j<row_x[i];j++)
		{
			if(row_rescomponent[i][j].sign==FALSE&&(((row_rescomponent[i][j].compshape.maxx-row_rescomponent[i][j].compshape.minx+1)/
				(row_rescomponent[i][j].compshape.maxy-row_rescomponent[i][j].compshape.miny+1)>0.8&&
				row_rescomponent[i][j].compshape.maxy-row_rescomponent[i][j].compshape.miny+1<temp_hei*0.9)||
				row_rescomponent[i][j].compshape.maxy-row_rescomponent[i][j].compshape.miny+1<temp_hei*0.6))
			{
				k++;
			}
		}
		if((double)k/row_x[i]>0.7)
		{
			row_x[i]=0;
			continue;
		}
		//ȥ����ַ�����Ͻǵ�������Ϣ�ĸ���--20100113
		if(i==0&&row_rescomponent[i][0].compshape.minx>lWidth*2/3)
		{
			row_x[i]=0;
			continue;
		}
		//ȥ������
		if(row_x[i]>18&&row_rescomponent[i][row_x[i]-1].compshape.maxx-row_rescomponent[i][0].compshape.minx<row_x[i]*temp_wid*2/3)
		{
			row_x[i]=0;
			continue;
		}
		//����ǰ�����ر���ȥ������
		k=0;
		jj=0;
		kk=0;
		for(j=0;j<row_x[i];j++)
		{
			if(row_rescomponent[i][j].sign==FALSE)
			{
				k++;
				jj+=row_rescomponent[i][j].pixelnum;
				kk+=(row_rescomponent[i][j].compshape.maxx-row_rescomponent[i][j].compshape.minx+1)*(row_rescomponent[i][j].compshape.maxy-row_rescomponent[i][j].compshape.miny+1);
			}
		}
		if((double)jj/kk>0.7)
		{
			row_x[i]=0;
			continue;
		}

	}

}

//������������ͨԪ,�����Ƿ��ǵ绰
bool Comp_Post(CodeComponent row_rescomponent[Num_line][Num_cha], int i, int j, int *row_x, int row_y)
{
	int m,n;
	int hei1,hei2;
	int kong1,kong2;
	bool post=true;
	if(row_rescomponent[i][j+10].compshape.maxx-row_rescomponent[i][j].compshape.minx<temp_wid*6)
	{
		post=false;
		return post;
	}
	for(m=j;m<j+11;m++)
	{
		if(row_rescomponent[i][m].sign==TRUE)
		{
			post=false;
			return post;
		}
	}
	for(m=j;m<j+4;m++)
	{
		kong1=(row_rescomponent[i][m+1].compshape.minx+row_rescomponent[i][m+1].compshape.maxx)/2-(row_rescomponent[i][m].compshape.minx+row_rescomponent[i][m].compshape.maxx)/2;
		kong2=(row_rescomponent[i][m+2].compshape.minx+row_rescomponent[i][m+2].compshape.maxx)/2-(row_rescomponent[i][m+1].compshape.minx+row_rescomponent[i][m+1].compshape.maxx)/2;
		if(abs(kong2-kong1)>kong1||abs(kong2-kong1)>kong2)
		{
			post=false;
			break;
		}
	}
	if(post==true)
	{
		for(m=j;m<j+10;m++)
		{
			for(n=j+1;n<j+11;n++)
			{
				hei1=row_rescomponent[i][m].compshape.maxy-row_rescomponent[i][m].compshape.miny;
				hei2=row_rescomponent[i][n].compshape.maxy-row_rescomponent[i][n].compshape.miny;
				if(abs(hei2-hei1)>hei1/8+1||abs(hei2-hei1)>hei2/8+1)
				{
					post=false;
					m=j+10;
					break;
				}
			}
		}
	}
	if(post==true)
	{
		for(m=j;m<j+11;m++)
		{
			if(row_rescomponent[i][m].compshape.maxy-row_rescomponent[i][m].compshape.miny+1==0)
				break;
			if((double)(row_rescomponent[i][m].compshape.maxx-row_rescomponent[i][m].compshape.minx+1)/
				(row_rescomponent[i][m].compshape.maxy-row_rescomponent[i][m].compshape.miny+1)>0.9)
			{
				post=false;
				break;
			}
		}
	}
	//�ų������������ֻ����֣�����11����
	if(post==true&&j+11<row_x[i])
	{
		kong1=(row_rescomponent[i][j+10].compshape.minx+row_rescomponent[i][j+10].compshape.maxx)/2-(row_rescomponent[i][j+9].compshape.minx+row_rescomponent[i][j+9].compshape.maxx)/2;
		kong2=(row_rescomponent[i][j+11].compshape.minx+row_rescomponent[i][j+11].compshape.maxx)/2-(row_rescomponent[i][j+10].compshape.minx+row_rescomponent[i][j+10].compshape.maxx)/2;
		if(abs(kong2-kong1)<kong1/2&&abs(kong2-kong1)<kong2/2)
		{
			post=false;
		}
	}

	return post;
}
//����ʶ��������
char vote_win(char* rec, int len)
{
	int result=10;
	char res;
	int i;
	int cha[11];
	for(i=0;i<11;i++)
		cha[i]=0;
	for(i=0;i<len;i++)
	{
		if(rec[i]>47&&rec[i]<58)
			cha[rec[i]-48]++;
		else
			cha[10]++;
	}
	for(i=0;i<10;i++)
	{
		if(cha[i]>len/2)
		{
			result=i;
			break;
		}
	}
	if(i==10)
	{
		for(i=0;i<10;i++)
		{
			if(cha[i]+cha[10]==len&&cha[i]>len/3)
			{
				result=i;
				break;
			}
		}
		if(i==10)
			result=10;
	}
	if(result==10)
		res='R';
	else
//		itoa(result,&res,10);
	  sprintf(&res, "%x", result);
	return res;

}
//ͳ�Ƶ�ַ����ͨԪ�Ŀ��
void WidHei_Comp(CodeComponent row_rescomponent[Num_line][Num_cha],int *row_x,int row_y,int &temp_wid,int &temp_hei)
{
	int i,j;
	int Hist_wid[200];//��ͨԪ���ֱ��ͼ
	int Hist_hei[200];//��ͨԪ�߶�ֱ��ͼ
	memset(Hist_wid, 0, 200*sizeof(int));
	memset(Hist_hei, 0, 200*sizeof(int));
	int wid,hei;//��ͨԪ���
	int num_wid, num_hei;//���к����ߵ���ͨԪ�ĸ���
	num_wid=0;
	num_hei=0;
	CRect rect;

	//*******ͳ����Ч��ͨԪ�Ŀ��*********************
	for(i=0;i<row_y;i++)
	{
		for(j=0;j<row_x[i];j++)
		{
			if(row_rescomponent[i][j].sign==FALSE)
			{
				rect.left=row_rescomponent[i][j].compshape.minx;
				rect.right=row_rescomponent[i][j].compshape.maxx;
				rect.top=row_rescomponent[i][j].compshape.miny;
				rect.bottom=row_rescomponent[i][j].compshape.maxy;
				wid=rect.right-rect.left+1;
				hei=rect.bottom-rect.top+1;
				if(wid<(int)(100*scale)&&wid>(int)(15*scale)&&wid<hei*7/4&& (float)row_rescomponent[i][j].pixelnum/(float)(wid*hei)<0.7)//(200910)
				{
					Hist_wid[wid]++;
					num_wid++;
				}
				if(hei<(int)(100*scale)&&hei>(int)(10*scale)&& (float)row_rescomponent[i][j].pixelnum/(float)(wid*hei)<0.7)//(200910)
				{
					Hist_hei[hei]++;
					num_hei++;
				}
			}
		}
	}

	int sum_wid=0;
	temp_wid=0;
	for(i=199;i>0;i--)
	{
		sum_wid+=Hist_wid[i];
		if(sum_wid>num_wid/3)//||(sum_wid>num_wid/4&&Hist_wid[i-1]==0&&Hist_wid[i-2]==0))
		{
			temp_wid=i;
			break;
		}
	}
	int sum_hei=0;
	temp_hei=0;
	for(i=199;i>0;i--)
	{
		sum_hei+=Hist_hei[i];
		if(sum_hei>num_hei/3)
		{
			temp_hei=i;
			break;
		}
	}
	if((double)temp_wid/temp_hei<4.0/5&&temp_wid<35)
		temp_wid=temp_hei*4/5;
	else if((double)temp_wid/temp_hei<4.0/5)
		temp_hei=temp_wid*5/4;


	//*******ͳ����Ч��ͨԪ�Ŀ��******����*********

}
////���߿�
//void DrawFrame(CDC* pDC,int *proImage,
//			   CRect charRect,unsigned int linewidth,
//			   COLORREF color,int lWidth,int lHeight)
//{
//	CPen pen;
//	pen.CreatePen (PS_SOLID,linewidth,color);
//	pDC->SelectObject (&pen);
//	::SelectObject (*pDC,GetStockObject(NULL_BRUSH));
//	CRect rect;
//	int* lpDIB=(int*) proImage;
//
//	rect= charRect;
//
//	pDC->Rectangle (rect);
//}
//ͼ������ȡ�ֻ�����
int PhoneNum_Extraction(int* bImage, int& lWidth,int& lHeight, char* cphone,int flag)//CString &phonenum)
{
//    int thresholdvalue=Otsu (bImage, lWidth, lHeight, 0, 0, lWidth, lHeight);


	LOGI("begin=%d",lHeight);
	scale=1;
    int i,j,k;
	showLen=lWidth;
	showWid=lHeight;
	memcpy(proImage,bImage,lWidth*lHeight*sizeof(int));
	memcpy(proImage_gray,proImage,lWidth*lHeight*sizeof(int));

	//��ֵ������
	int backcolor=0;
	BOOL reflect=FALSE;
	RECT barRect = {-1,-1,-1,-1};



	ThresholdOtsu_add(proImage,  showLen, showWid,backcolor,barRect);
	//��Ӻ�����Ԥ��ȡ����
	int iTop=0;
	int iBottom=showWid;
	PreSegment_Line(proImage,showLen,showWid,iTop,iBottom);

	//****�����൱��mpsocr�����к���Address_Process()����
	//��ֵ��ͼ���Ԥ�����ų���ֱ���ߣ�
	int iLen_Line=0;
	iLen_Line=(iBottom-iTop)*5/4;
	if(iLen_Line>showWid-1)
		iLen_Line=showWid-1;
	else if(iLen_Line<3)
		iLen_Line=showWid-1;
	LOGI("iLen_Line=%d",iLen_Line);
	int run_num;
	for(i=0;i<showLen/3;i++)
	{
		run_num=0;
		for(j=0;j<showWid;j++)
		{
			if(proImage[j*showLen+i]==0)
				run_num++;
			else
			{
				if(run_num>iLen_Line)
				{
					for(k=j;k>j-run_num;k--)
						proImage[k*showLen+i]=255;
					run_num=0;
				}
				if(run_num!=0)
					run_num=0;
			}
		}
		if(run_num>iLen_Line)
		{
			for(k=j;k>j-run_num;k--)
				proImage[k*showLen+i]=255;
			run_num=0;
		}
		if(run_num!=0)
			run_num=0;
	}
	for(i=showLen*2/3;i<showLen;i++)
	{
		run_num=0;
		for(j=0;j<showWid;j++)
		{
			if(proImage[j*showLen+i]==0)
				run_num++;
			else
			{
				if(run_num>iLen_Line)
				{
					for(k=j;k>j-run_num;k--)
						proImage[k*showLen+i]=255;
					run_num=0;
				}
				if(run_num!=0)
					run_num=0;
			}

		}
		if(run_num>iLen_Line)
		{
			for(k=j;k>j-run_num;k--)
				proImage[k*showLen+i]=255;
			run_num=0;
		}
		if(run_num!=0)
			run_num=0;
	}
	//�ų�ˮƽ����
	iLen_Line=iLen_Line*2;//shujing20161122
	for(i=0;i<showWid;i++)
	{
		run_num=0;
		for(j=0;j<showLen;j++)
		{
			if(proImage[i*showLen+j]==0)
				run_num++;
			else
			{
				if(run_num>iLen_Line)
				{
					for(k=j;k>j-run_num;k--)
						proImage[i*showLen+k]=255;
					run_num=0;
				}
				if(run_num!=0)
					run_num=0;
			}
		}
		if(run_num>iLen_Line)
		{
			for(k=j;k>j-run_num;k--)
				proImage[i*showLen+k]=255;
			run_num=0;
		}
		if(run_num!=0)
			run_num=0;
	}
	//
	if(iBottom-iTop>20)
	{
		for(i=0;i<iTop;i++)

		{
			for(j=0;j<showLen;j++)
				proImage[i*showLen+j]=255;
		}
		for(i=iBottom;i<showWid;i++)
		{
			for(j=0;j<showLen;j++)
				proImage[i*showLen+j]=255;
		}
	}
	LOGI("hei=%d",showWid);
	//���������ͼ��������Ŵ���
	if((showWid>200&&(iBottom-iTop)>80)||(showWid>300&&(iBottom-iTop)>60))//||showWid>300  20170422
	{
		scale=(float)(iBottom-iTop)/60;
        Zoom_IAddress(proImage,proImage_gray, showLen, showWid, scale);
        //	lWidth=showLen;
	//	lHeight=showWid;
		scale=1;
	}

	#ifdef DIR_ADDREC
	char path[100] = "D:\\add_thresh2.jpg";
	CImgFile *dFile = IfxCreateImgFile(path);
	if(dFile!=NULL)
		dFile->WriteFile(proImage,showLen, showWid,8);
	delete dFile;

	#endif

	//��ֵ������
//	ThresholdOtsu(proImage, showLen, showWid);
//	DWORD totaltime;
//	totaltime=GetTickCount();
	//��ͨԪ�궨
	int digi=0;
	CodeComponent rescomponent[200];
	digi=ConCompLabelling8(proImage,showLen, showWid,rescomponent,FALSE);

	//�����쳣�����ͨԪ
	int WidThresh, HeiThresh;
	WidThresh=showLen/2;
	HeiThresh=showWid/2;
	for(i=0;i<digi;i++)
	{
		if(rescomponent[i].compshape.maxx-rescomponent[i].compshape.minx>WidThresh)
		{
			rescomponent[i].sign=TRUE;
		}
	}
	//������ͨԪ
/*	CRect rect;

	for(i=0;i<digi;i++)
	{
		if(rescomponent[i].sign==FALSE)
		{
		rect.left=rescomponent[i].compshape.minx;
		rect.right=rescomponent[i].compshape.maxx;
		rect.top=rescomponent[i].compshape.miny;
		rect.bottom=rescomponent[i].compshape.maxy;

		DrawFrame(pDC,proImage,rect,2,RGB(0,0,255),showLen, showWid);
		}
	}*/

	//�ϲ��ཻ�������ϵ����ͨԪΪ����
	Comp_in( proImage, showLen, showWid, rescomponent, digi);

	//�Թ�����ͨԪ���½��д���
	reprocess(proImage_gray, showLen, showWid, rescomponent, digi);

	//����ɾ�������ͨԪ
/*	for(i=0;i<digi;i++)
	{
		if(rescomponent[i].pixelnum>800&&rescomponent[i].compshape.maxx-rescomponent[i].compshape.minx<rescomponent[i].compshape.maxy-rescomponent[i].compshape.miny)
		{
			rescomponent[i].sign=TRUE;
		}
	}
	for(i=0;i<digi;i++)
	{
		if(rescomponent[i].sign==FALSE)
		{
			rect.left=rescomponent[i].compshape.minx;
			rect.right=rescomponent[i].compshape.maxx;
			rect.top=rescomponent[i].compshape.miny;
			rect.bottom=rescomponent[i].compshape.maxy;

			DrawFrame(pDC,proImage,rect,2,RGB(0,255,0),showLen, showWid);
		}

	}*/
	//�зָ�
	CodeComponent row_rescomponent[Num_line][Num_cha];
	//�Ѷ�ֵ��������ͼ�񴫻�
	LOGI("showLen=%d",showLen);
	LOGI("lWidth=%d",lWidth);
	if(showLen!=lWidth)
	{
		memset(bImage,255,lWidth*lHeight*sizeof(int));
		for(i=0;i<showWid;i++)
		{
			for(j=0;j<showLen;j++)
			{
				bImage[i*lWidth+j]=proImage[i*showLen+j];
			}
		}
	}
	else
		memcpy(bImage,proImage,showLen*showWid*sizeof(int));


	int row_y=0;
	int row_x[Num_line];//�б�
	memset(row_x,0,Num_line*sizeof(int));

	row_y=row_segment(proImage, showLen, showWid, rescomponent, digi, row_rescomponent,row_x);


	LOGI("row_y=%d",row_y);
	//�ϲ����ֲ���Ϊ����(Ĭ�����ϲ�4��)
	int jj;
	parts_merge(row_rescomponent,row_x,row_y);
	for(i=0;i<row_y;i++)
	{
		k=0;
		for(j=row_x[i]-1;j>=0;j--)
		{
			if(row_rescomponent[i][j].sign==TRUE||row_rescomponent[i][j].pixelnum<35)
			{
				k++;
				for(jj=j;jj<row_x[i]-k;jj++)
					row_rescomponent[i][jj]=row_rescomponent[i][jj+1];
			}
		}
		row_x[i]=row_x[i]-k;
	}
	LOGI("2 row_y=%d",row_y);
//	totaltime=GetTickCount()-totaltime;
/*	for(i=0;i<row_y;i++)
	{
		for(j=0;j<row_x[i];j++)
		{
			if(row_rescomponent[i][j].sign==FALSE)
			{
				rect.left=row_rescomponent[i][j].compshape.minx;
				rect.right=row_rescomponent[i][j].compshape.maxx;
				rect.top=row_rescomponent[i][j].compshape.miny;
				rect.bottom=row_rescomponent[i][j].compshape.maxy;

				DrawFrame(pDC,proImage,rect,2,RGB(80*(i+1),0,0),showLen,showWid);
			}
		}
	}*/
	//ɾ���ǵ�ַ��
//	row_recognition(showLen, showWid, row_rescomponent,row_x,row_y);

	//����ͳ�Ƶ�ַ���ַ����
	WidHei_Comp(row_rescomponent,row_x,row_y, temp_wid, temp_hei);


	//�����б�
	int mfont=1;
	if(mfont!=1)
	{
		int num_j=0;
		int index_j=0;
		for(i=0;i<row_y;i++)
		{
			if(row_x[i]>num_j&&row_x[i]<Num_cha-1)
			{
				num_j=row_x[i];
				index_j=i;
				if(num_j>10)
					break;
			}
		}
		int Font_C=0;
		if(num_j>4)//2009-11-05
		{
			Font_C=FontRecognition(row_rescomponent[index_j],row_x[index_j]);
			if(Font_C==2)
			{
				int index_t=index_j;
				index_j=num_j=0;
				for(i=0;i<row_y;i++)
				{
					if((row_x[i]>num_j&&i!=index_t&&row_x[i]<30)||(row_x[i]==num_j&&index_j==0&&row_x[i]<35))//20091111
					{
						num_j=row_x[i];
						index_j=i;
						if(num_j>10)
							break;
					}
				}
				Font_C=FontRecognition(row_rescomponent[index_j],row_x[index_j]);
			}

			if(Font_C>0)
			{
				mfont=2;//��д��
				return -1;
			//	pDC->TextOut(0,showWid+10,"��д��ַ��");
			}
			else
				mfont=1;
		}
		else
		{
			mfont=2;//��д��
			return -2;
		//	pDC->TextOut(0,showWid+50,"û���ҵ��ı���");
		}
	}


//	pInfo->mWinResult.bcharScore=1;

	int win_code=0;
//	CString phonenum="";
	LOGI("3 row_y=%d",row_y);
	for(i=0;i<11;i++)
		cphone[i]=' ';
	cphone[11]='\0';
	if(mfont==1)
	{
		//ʶ���ַ��
		CString rec_str[Num_line];
		for(i=0;i<Num_line;i++)
			rec_str[i]="";

//		DWORD time;
//		time=GetTickCount();
		LOGI("3.1 row_y=%d",row_y);
		win_code=Address_Rec(proImage,showLen, showWid,row_rescomponent,row_x,row_y, rec_str);
		LOGI("4 row_y=%d",row_y);

		if(win_code==POSTCODE_LEN)
		{
			//show win-postcode
			for(i=0;i<POSTCODE_LEN;i++)
			{
				cphone[i]=(char)Win_Postcode[i];
				//	phonenum+=(char)Win_Postcode[i];
//				if(Win_Postcode[i]>47&&Win_Postcode[i]<58)//hxc20170501
//					cphone[i]=(char)Win_Postcode[i];
//				else
//				{
//					cphone[0]='\0';
//					return 0;
//				}

			}

			cphone[0]='1';
			cphone[11]='\0';

			return 11;
		//	pDC->TextOut(200,showWid+10,phonenum);

		}
		else
		{
			char cc;
			int nn=0;
			for(i=0;i<row_y;i++)
			{
				nn=0;
				LOGI("leng=%d",rec_str[i].GetLength());
				LOGI("rec_str=%s",rec_str[i].GetString());
//				if(rec_str[i].GetLength()>10)//hxc 20170523
//				{
					for(j=0;j<rec_str[i].GetLength();j++)
					{
						cc=rec_str[i].GetAt(j);
						cphone[nn++]=cc;
					}
					cphone[j]='\0';
					return 11;
				/*	for(j=0;j<rec_str[i].GetLength();j++)
					{
						cc=rec_str[i].GetAt(j);
						if(cc>='0'&&cc<='9')
						{
							cphone[nn]=cc;
							nn++;

						//	phonenum+=cc;
						}
						else
						{
							if(nn==11)
							{
								cphone[nn]='\0';
								break;
							}
							else
							{
								nn=0;
								//	phonenum="";
							}
						}
					}
					if(nn<11)
					{
						for(j=0;j<11;j++)
							cphone[j]=' ';
						cphone[11]='\0';
						//	phonenum="";
					}

					else
					{
					//	pDC->TextOut(200,showWid+10,phonenum);
					//	break;
						cphone[nn]='\0';
						return 11;
					}*/
				//}
			}
		}


	}
	return 0;


}
//������Ԥ��ȡ
void PreSegment_Line(int* proImage, int showLen, int showWid, int& iTop, int& iBottom)
{
	int i,j;
	int iLen;
	int iMaxLen;

	//���ϱ�Ե
	iTop=0;
	for(i=showWid/2;i>0;i--)
	{
		iLen=0;
		iMaxLen=0;
		for(j=0;j<showLen;j++)
		{
			if(proImage[i*showLen+j]==255)
				iLen++;
			else if(proImage[i*showLen+j]==0&&iLen>0)
			{
				if(iLen>iMaxLen)
					iMaxLen=iLen;
				iLen=0;
			}

		}
		if(iLen>iMaxLen)
			iMaxLen=iLen;
		if(iMaxLen>showLen*2/3)
		{
			iTop=i-1;
			break;
		}
	}
	//���±�Ե
	iBottom=showWid-1;
	for(i=showWid/2;i<showWid-1;i++)
	{
		iLen=0;
		iMaxLen=0;
		for(j=0;j<showLen;j++)
		{
			if(proImage[i*showLen+j]==255)
				iLen++;
			else if(proImage[i*showLen+j]==0&&iLen>0)
			{
				if(iLen>iMaxLen)
					iMaxLen=iLen;
				iLen=0;
			}

		}
		if(iLen>iMaxLen)
			iMaxLen=iLen;
		if(iMaxLen>showLen*2/3)
		{
			iBottom=i+1;
			break;
		}
	}

}
bool Comp_Phone(character1* chara, int j)
{
	int i;
	bool bRes=true;
	int kong1,kong2;
	for(i=j;i<j+11;i++)
	{
		if(chara[i].type_c!=1)
		{
			bRes=false;
			break;
		}
	}
	if(bRes==true)
	{
		for(i=j;i<j+9;i++)
		{
			kong1=(chara[i+1].rect_c.left+chara[i+1].rect_c.right)/2-(chara[i].rect_c.left+chara[i].rect_c.right)/2;
			kong2=(chara[i+2].rect_c.left+chara[i+2].rect_c.right)/2-(chara[i+1].rect_c.left+chara[i+1].rect_c.right)/2;
			if(abs(kong2-kong1)>kong1/2||abs(kong2-kong1)>kong2/2||kong1>chara[i+1].rect_c.bottom-chara[i+1].rect_c.top||kong2>chara[i+2].rect_c.bottom-chara[i+2].rect_c.top)//20170327
			{
				bRes=false;
				break;
			}

		}
	}
	return bRes;

}
