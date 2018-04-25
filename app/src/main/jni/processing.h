#ifndef _INC_processingAPI
#define _INC_processingAPI

struct CRect{
	int left;   
	int right;     
	int top;          
	int bottom;        
};
typedef enum { FALSE, TRUE }BOOL;
//º¯ÊýÉùÃ÷
CRect CharSegment(int * proImage, int lWidth, int lHeight);
void StdDIBbyRect(int* proImage,CRect &m_charRect,int lWidth,int lHeight,
				  int tarWidth, int tarHeight);

bool DeleteScaterJudge(int* image,int* lplab, int lWidth, int lHeight, int x, int y, int lab[], int& m_lianxushu,int lianXuShu);
void RemoveScatterNoise(int* image, int lWidth,int lHeight,int &m_lianxushu);

#endif
