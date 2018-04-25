#include "CString.h"
#include <string.h>
using namespace std;
//构造函数
CString::CString(){

}
CString::CString(const CString& str){
	strLength = str.strLength;
	p_str = new char[strLength + 1];
	strcpy(p_str, str.p_str);
}
CString::CString(const char* str){
	if (str == NULL){
		return;
	}
	strLength = strlen(str);
	p_str = new char[strLength + 1];
	strcpy(p_str, str);
}

int CString::GetLength(){
	return strLength;
}
char CString::GetAt(int index){
	return p_str[index];
}
//'='赋值操作符
const CString & CString::operator =(const CString& str){
	if (this != &str){
		if (strLength<str.strLength){
			delete[] p_str;
			p_str = new char[str.strLength + 1];
		}
		strLength = str.strLength;
		strcpy(p_str, str.p_str);
	}

	return *this;
}
const CString & CString::operator =(const char * str){
//	  if (str == NULL){
//			return NULL;
//		}
		strLength = strlen(str);
		p_str = new char[strLength + 1];
		strcpy(p_str, str);
		return *this;
}
//'+='操作符
const CString& CString::operator+=(const CString& str){
	char *temp = p_str;
	strLength = strLength + str.strLength;
	p_str = new char[strLength + 1];

	strcpy(p_str, temp);
	strcat(p_str, str.p_str);
	delete[]temp;

	return *this;
}
const CString & CString::operator +=(const char* str){
	char *temp = p_str;
		strLength = strLength + strlen(str);
		p_str = new char[strLength + 1];

		strcpy(p_str, temp);
		strcat(p_str,str);
		delete[]temp;

		return *this;
}
const CString& CString::operator+=(char str){
//	p_str = new char[strLength + 1];
//    p_str[strLength]=str;
//    p_str[strLength+1]=0;
//    strLength++;

	 p_str[strLength++]=str;
     p_str[strLength]=0;

	return *this;
}
char* CString::GetString(){
	return p_str;
}


