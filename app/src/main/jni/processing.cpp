#include "processing.h"
#include <math.h>
#include<string.h>
#include <malloc.h>

//�ַ��ָ�
CRect CharSegment(int * proImage, int lWidth, int lHeight)
{
	
//�����������ÿ���ַ����������
	CRect charRect1;
	charRect1.left=0;
	charRect1.right=lWidth-1;
	charRect1.top=0;
	charRect1.bottom=lHeight-1;
    //���صĻҶ�ֵ
    int gray; 

	//����ѭ������
	int i,j;
	//��������ɨ�裬�ҵ��ϱ߽�

	//��
	for (i=0;i<lHeight;i++)
	{
         //��
  		for (j=0;j<lWidth;j++)
		{
            
			//��øõ�ĻҶ�ֵ
			gray = proImage[i*lWidth+j];

			//���Ƿ�Ϊ�ڵ�
			if (gray == 0)
			{   
               //��Ϊ�ڵ㣬�Ѵ˵���Ϊ�ַ����µ���ߵ�
				charRect1.top = (i==0)?i:i-1;

				//��iǿ�и�ֵ���ж�ѭ��
				i=lHeight;

				//����ѭ��
				break;
			}

        //����õ㲻�Ǻڵ㣬����ѭ��
		}
	}


    //��������ɨ�裬���±߽�

	//��
	for (i = lHeight-1;i>=0;i--)
    {

		//��
		for (j=0;j<lWidth;j++)
		{
			
			//��ȡ�õ�ĻҶ�ֵ
			gray = proImage[i*lWidth+j];


			//�ж��Ƿ�Ϊ�ڵ�
			if (gray == 0)
			{
				//��Ϊ�ڵ㣬�Ѵ˵���Ϊ�ַ����µ���͵�
				charRect1.bottom =(i==lHeight-1)?i: i+1;

				//��iǿ�и�ֵ���ж�ѭ��
				i=-1;

				//����ѭ��
				break;
			}

          //����õ㲻�Ǻڵ㣬����ѭ��
		}
	
	}

   
	//��
	for (i=0;i<lWidth;i++)
	{
		for (j=0;j<lHeight;j++)
		{	
			//��ȡ�õ�ĻҶ�ֵ
			gray = proImage[lWidth*j+i];

            //�ж��Ƿ�Ϊ�ڵ�
			if (gray == 0)
			{
				charRect1.left = (i==0)?i:i-1;

				i=lWidth;
				break;
				
			}		
		}
	}
	for (i=lWidth-1;i>=0;i--)
	{
		for (j=0;j<lHeight;j++)
		{	
			//��ȡ�õ�ĻҶ�ֵ
			gray = proImage[lWidth*j+i];

            //�ж��Ƿ�Ϊ�ڵ�
			if (gray == 0)
			{
				charRect1.right = (i==lWidth-1)?i:i+1;

				i=-1;
				break;
				
			}		
		}
	}

	//������1����
	return charRect1;
}


//��һ������
void StdDIBbyRect(int* proImage,CRect &m_charRect,int lWidth,int lHeight,int tarWidth, int tarHeight)
{	

	// ѭ������
	int	i;
	int	j;
	
	//��ȡ��߶ȷ����ϵ���������
	double wscale,hscale;

	//����һ����ʱ������,����ű仯���ͼ����Ϣ
	int image[200*200];
	memset(image,255,lWidth*lHeight*sizeof(int));
    
	//����ӳ��������������
	int i_src,j_src;

	//����ַ�λ����Ϣ�Ľṹ��
	CRect rect;
	CRect rectnew;

	rect= m_charRect;

	//������������

	//�����귽�����������
	wscale=(double)tarWidth/(rect.right-rect.left+1);
	//�����귽�����������
	hscale=(double)tarHeight/(rect.bottom-rect.top+1);

	//�����׼������

	//�ϱ߽�
	rectnew.top =rect.top ;

	//�±߽�
	rectnew.bottom =rect.top +tarHeight;

	//��߽�
	rectnew.left =rect.left ;

	//�ұ߽�
	rectnew.right =rectnew.left +tarWidth;

	//��ԭ���ο��ڵ�����ӳ�䵽�µľ��ο���
	for(i=rectnew.top ;i<rectnew.bottom ;i++)
	{
		for(j=rectnew.left ;j<rectnew.right ;j++)
		{   
			//����ӳ������
			i_src=rectnew.top +(int)((i-rectnew.top )/hscale);
			j_src=rectnew.left +(int)((j-rectnew.left )/wscale);

			//�����Ӧ�����ص����ӳ�����
			
			image[lWidth*i+j]=proImage[lWidth*i_src+j_src];
		}
	}
	
    m_charRect=rectnew;
	//�������������ݿ�����ͼ�����������
	memcpy(proImage,image,lHeight*lWidth*sizeof(int));

	
}

//ɾ����ɢ��
void RemoveScatterNoise(int* image, int lWidth,int lHeight,int &m_lianxushu)
{
	//�����ж������ĳ�����ֵΪ15
	//������뿼��������ӵĺڵ����ĿС��15����Ϊ�������������
	int length=8;
	
	// ѭ������
	m_lianxushu=0;
	int	i;
	int	j;	
	int k;

	//����һ��������ű�־���ڴ�����
//	int *lplab = new int[lHeight * lWidth*sizeof(int)];
	int *lplab;
	lplab=(int*)malloc(lHeight * lWidth*sizeof(int));

	//����һ������������ɢ�ж�������ڴ�����
	//bool *lpTemp = new bool[lHeight * lWidth*sizeof(bool)];
	bool *lpTemp;
	lpTemp=(bool*)malloc(lHeight * lWidth*sizeof(bool));

    //��ʼ����־����
	for (i=0;i<lHeight*lWidth;i++)
    {

    //�����еı�־λ����Ϊ��
	lplab[i] = 0;

	}

	//���������ɢ������������
	int lab[25];
	
	//Ϊѭ����������ʼֵ
	k=0;

	//ɨ������ͼ��

	//����ɨ��
	for(i =0;i<lHeight;i++)
	{  
       
	   //����ɨ��
		for(j=0;j<lWidth;j++)
			{	
				//�Ȱѱ�־λ��false
				for(k=0;k<m_lianxushu;k++)
				lplab[lab[k]] = 0;

				//��������0
				m_lianxushu =0;

			    //������ɢ���ж�
			    lpTemp[i*lWidth+j] = DeleteScaterJudge(image,lplab,lWidth,lHeight,j,i,lab,m_lianxushu,length);

			}
	}
			
	//ɨ������ͼ�񣬰���ɢ�����ɰ�ɫ

	//����ɨ��
	for(i = 0;i<lHeight;i++)
	{

      //����ɨ��
		for(j=0;j<lWidth;j++)
		{       
			    //�鿴��־λ,���Ϊ���򽫴˵���Ϊ�׵�
				if(lpTemp[i*lWidth+j] == false)
				{	
                    //����������Ϊ�׵�
					image[i*lWidth+j]=255;
					
				}
			}
	}
//	delete []lplab;
//	delete []lpTemp;
	free(lplab);
	free(lpTemp);

}
bool DeleteScaterJudge(int* image,int* lplab, int lWidth, int lHeight, int x, int y, int lab[], int& m_lianxushu,int lianXuShu)
{
	
	//���������������Ҫ��˵��������ɢ�㣬����
	if(m_lianxushu>=lianXuShu)
		return true;

	//���ȼ�һ
	m_lianxushu++;

	//�趨���ʱ�־
	lplab[lWidth * y +x] = 1;
	
	//������ʵ�����
//	lab[m_lianxushu-1].x = x;
	//lab[m_lianxushu-1].y = y;
	lab[m_lianxushu-1]=y*lWidth+x;
	//���صĻҶ�ֵ
	int gray;
  
	//�����ж�
    //���������������Ҫ��˵��������ɢ�㣬����
	if(m_lianxushu>=lianXuShu)
		return true;
	
	//�������ݹ�
	else
	{	
		//�������������Լ����ϡ����ϡ����¡����°˸�����
		//����Ǻ�ɫ�㣬����ú���������еݹ�

		//���������
		
		//���ݻҶ�ֵ
		gray=image[(y-1)*lWidth+x];

		//�������ͼ���ڡ���ɫΪ��ɫ����û�б����ʹ�
		if(y-1 >=0 && gray == 0 && lplab[(y-1)*lWidth+x] == 0)

		//���еݹ鴦��		
		DeleteScaterJudge(image,lplab,lWidth,lHeight,x,y-1,lab,m_lianxushu,lianXuShu);

		//�жϳ���
		//���������������Ҫ��˵��������ɢ�㣬����
		if(m_lianxushu>=lianXuShu)
		return true;
		
		//���µ�
		
        //���ݻҶ�ֵ
		gray=image[(y-1)*lWidth+x-1];

        //�������ͼ���ڡ���ɫΪ��ɫ����û�б����ʹ�
		if(y-1 >=0 &&  x-1 >=0 && gray== 0 && lplab[(y-1)*lWidth+x-1] == 0)

      	//���еݹ鴦��		
		DeleteScaterJudge(image,lplab,lWidth,lHeight,x-1,y-1,lab,m_lianxushu,lianXuShu);

        //�жϳ���
		//���������������Ҫ��˵��������ɢ�㣬����
		if(m_lianxushu>=lianXuShu)
		return true;
		
		//���
		
		//���ݻҶ�ֵ
		gray=image[y*lWidth+x-1];

        //�������ͼ���ڡ���ɫΪ��ɫ����û�б����ʹ�
		if(x-1 >=0 &&  gray== 0 && lplab[y*lWidth+x-1] == 0)

        //���еݹ鴦��		
		DeleteScaterJudge(image,lplab,lWidth,lHeight,x-1,y,lab,m_lianxushu,lianXuShu);

        //�жϳ���
		//���������������Ҫ��˵��������ɢ�㣬����
		if(m_lianxushu>=lianXuShu)
			return true;
		
		//����
		
		//���ݻҶ�ֵ
		gray=image[lWidth*(y+1)+x-1];

        //�������ͼ���ڡ���ɫΪ��ɫ����û�б����ʹ�
		if(y+1 <lHeight && x-1 >= 0 && gray == 0 && lplab[(y+1)*lWidth+x-1] == 0)

		//���еݹ鴦��
		
		DeleteScaterJudge(image,lplab,lWidth,lHeight,x-1,y+1,lab,m_lianxushu,lianXuShu);

        //�жϳ���
		//���������������Ҫ��˵��������ɢ�㣬����
		if(m_lianxushu>=lianXuShu)
			return true;
		
		//����
	
        //���ݻҶ�ֵ
		gray=image[lWidth*(y+1)+x];

        //�������ͼ���ڡ���ɫΪ��ɫ����û�б����ʹ�
		if(y+1 < lHeight && gray == 0 && lplab[(y+1)*lWidth+x] == 0)

        //���еݹ鴦��
		
		DeleteScaterJudge(image,lplab,lWidth,lHeight,x,y+1,lab,m_lianxushu,lianXuShu);

        //�жϳ���
		//���������������Ҫ��˵��������ɢ�㣬����
		if(m_lianxushu>=lianXuShu)
			return true;
		
		//����
		
		//���ݻҶ�ֵ
		gray=image[lWidth*(y+1)+x+1];

        //�������ͼ���ڡ���ɫΪ��ɫ����û�б����ʹ�
		if(y+1 <lHeight && x+1 <lWidth &&  gray == 0 && lplab[(y+1)*lWidth+x+1] == 0)

        //���еݹ鴦��
		DeleteScaterJudge(image,lplab,lWidth,lHeight,x+1,y+1,lab,m_lianxushu,lianXuShu);

        //�жϳ���
		//���������������Ҫ��˵��������ɢ�㣬����
		if(m_lianxushu>=lianXuShu)
			return true;
		
		//�ұ�
	
        //���ݻҶ�ֵ
		gray=image[lWidth*y+x+1];

		//�������ͼ���ڡ���ɫΪ��ɫ����û�б����ʹ�
		if(x+1 <lWidth && gray==0 && lplab[y*lWidth+x+1] == 0)

        //���еݹ鴦��		
		DeleteScaterJudge(image,lplab,lWidth,lHeight,x+1,y,lab,m_lianxushu,lianXuShu);

        //�жϳ���

		//���������������Ҫ��˵��������ɢ�㣬����
		if(m_lianxushu>=lianXuShu)
			return true;
		
		//����
		
        //���ݻҶ�ֵ
		gray=image[lWidth*(y-1)+x+1];

        //�������ͼ���ڡ���ɫΪ��ɫ����û�б����ʹ�
		if(y-1 >=0 && x+1 <lWidth && gray == 0 && lplab[(y-1)*lWidth+x+1] == 0)

       //���еݹ鴦��		
	   DeleteScaterJudge(image,lplab,lWidth,lHeight,x+1,y-1,lab,m_lianxushu,lianXuShu);

        //�жϳ���
		//���������������Ҫ��˵��������ɢ�㣬����
		if(m_lianxushu>=lianXuShu)
			return true;
	}
	

	//����ݹ����������false��˵������ɢ��
	return false;

}
