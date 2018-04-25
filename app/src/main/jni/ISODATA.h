// ISODATA.cpp : Defines the entry point for the console application.
//
#ifndef _ISODATA
#define _ISODATA

#include "stdio.h"
#include "math.h"
#define  eps   0.00001

struct Pointf
{
	int sequence;
	float x;
	float y;
};

struct PointZ
{
	float x;
	float y;
};

float CalDistancef(Pointf x1,Pointf x2)
{
	return sqrtf((x1.x-x2.x)*(x1.x-x2.x)+(x1.y-x2.y)*(x1.y-x2.y));
}

float CalDistanceZ(PointZ x1,PointZ x2)
{
	return sqrtf((x1.x-x2.x)*(x1.x-x2.x)+(x1.y-x2.y)*(x1.y-x2.y));
}

float CalDistancefZ(Pointf x1,PointZ x2)
{
	return sqrtf((x1.x-x2.x)*(x1.x-x2.x)+(x1.y-x2.y)*(x1.y-x2.y));
}




int isodata(int N, Pointf* pts, int &Nc, PointZ *ZArray, int* Nj,float ThetaC, int L, int I) 
{
	
	int i,j,m;
	
    Pointf **SAArray;
	SAArray=(Pointf**) malloc(N*sizeof(Pointf*));
	for(i=0;i<N;i++)
		SAArray[i]=(Pointf*)malloc(N*sizeof(Pointf));

	int    count=0;

	float  *Dij;
	Dij=(float*)malloc(N*N*sizeof(float));
	int    *Diji;
	Diji=(int*)malloc(N*N*sizeof(int));
	int    *Dijj;
	Dijj=(int*)malloc(N*N*sizeof(int));
	int    q=0;
	int    p=0;
    float ft;
	int   it;
	int   jt;
	int ss=0;
	PointZ Ztp;

	int cur=0;
	float dis,ftemp;
	int xx;
	ft=0;
	while(count<I&&ft<ThetaC)
	{
		for(i=0;i<N;i++)
		{
			Nj[i]=0;
		}
		//将模式样本归类	
		for(i=0;i<N;i++)                         
		{	
			dis=1.0e+10;
			xx=0;
		//	ftemp;
			
			for(j=0;j<Nc;j++)                     
			{
				ftemp=CalDistancefZ(pts[i],ZArray[j]);
				if(ftemp<dis||fabs(dis-ftemp)<eps)
				{
					xx=j;
					dis=ftemp;
				}
			}
			
			SAArray[xx][Nj[xx]].x=pts[i].x;
			SAArray[xx][Nj[xx]].y=pts[i].y;
			SAArray[xx][Nj[xx]].sequence=pts[i].sequence;	
			Nj[xx]=Nj[xx]+1;	
		}
		count++;
		//聚类后去除类内元素个数为零的类
		for(j=0;j<Nc;j++)               
		{
			if(Nj[j]==0)
			{
				
				i=j;
				int tr=j;
				while(j<Nc-1)
				{	
					for(m=0;m<Nj[j+1];m++)
					{
						SAArray[j][m].x=SAArray[j+1][m].x;
						SAArray[j][m].y=SAArray[j+1][m].y;
						SAArray[j][m].sequence=SAArray[j+1][m].sequence;
					} 
					j++;			
				}
				while(i<Nc-1)
				{
					Nj[i]=Nj[i+1];
					i++;
				}
				Nc--;
				j=tr-1;//20091118
			}		
		}
		
        //修正各聚类中心
		for(j=0;j<Nc;j++)
		{
			float temx=0,temy=0;
			for(i=0;i<Nj[j];i++)
			{
				temx+=SAArray[j][i].x;
				temy+=SAArray[j][i].y;		
			}
			if(Nj[j]!=0)//20091118
			{
				ZArray[j].x=temx/Nj[j];
				ZArray[j].y=temy/Nj[j];
			}
		}
		
		//计算全部聚类中心的距离
		ss=0;
		for(i=0;i<Nc-1;i++)
		{
			for(j=i+1;j<Nc;j++)
			{
				Dij[ss]=CalDistanceZ(ZArray[i],ZArray[j]);
				Diji[ss]=i;
				Dijj[ss]=j;
				ss++;		
			}
		}
		
		//简单起见，只考虑一次只合并一对聚类中心的情况
		//找出类间距离最小的
		ft=Dij[0];
		it=Diji[0];
		jt=Dijj[0];
		for(i=1;i<ss;i++)
		{
			if(Dij[i]<ft)
			{
				ft=Dij[i];
				it=Diji[i];
				jt=Dijj[i];
			}
		}
		
		if(ft<ThetaC&&Nc>1)
		{
			Ztp.x=(Nj[it]*ZArray[it].x+Nj[jt]*ZArray[jt].x)/(Nj[it]+Nj[jt]);
			Ztp.y=(Nj[it]*ZArray[it].y+Nj[jt]*ZArray[jt].y)/(Nj[it]+Nj[jt]);
			ZArray[it].x=Ztp.x;
			ZArray[jt].y=Ztp.y;
			
			j=jt;
			while(j<Nc-1)
			{
				ZArray[j].x=ZArray[j+1].x;
				ZArray[j].y=ZArray[j+1].y;
				j++;
			}
			j=jt;
			
			while(j<Nc-1)
			{
				Nj[j]=Nj[j+1];
				j++;
			}
			Nc--;
		}
	}
	
	count=0;
	for (i = 0; i < N; i++)
	{
		free(SAArray[i]); 
	}
	free(SAArray);

	free(Dij);
	free(Diji);
	free(Dijj);

	return Nc;


}
#endif
