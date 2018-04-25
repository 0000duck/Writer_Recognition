#ifndef _INC_CSTRING_
#define _INC_CSTRING_

#include <string.h>
class CString {
public:
	//���캯��
	CString();
	CString(const CString&);
	CString(const char*);

	int  GetLength();
	char GetAt(int index);
	char* GetString();

	//'='��ֵ������
	const CString& operator=(const CString&);
	const CString& operator=(const char*);
	//'+='������
	const CString& operator+=(const CString&);
	const CString& operator+=(char);
	const CString& operator+=(const char*);

	// const CString& operator=(const CString& stringSrc);
	// const CString& operator=(LPCTSTR lpsz);
	// const CString& operator=(TCHAR ch);
	// const CString& operator+=(const CString& string);
	// const CString& operator+=(TCHAR ch);
	// const CString& operator+=(LPCTSTR lpsz);

private:
	int strLength;
	char*p_str;

};
#endif // !_INC_CSTRING_
