#ifdef FOO
#ifdef G_OS_WIN32
#define g_get_user_name g_get_user_name_utf8
#endif

char* g_get_user_name(void);
#endif


#ifndef G_OS_WIN32
void* g_get_user_name(void);
#endif

#ifdef G_OS_WIN32
char* g_get_user_name(void);
#endif

#ifdef G_OS_WIN32
#ifdef BAR
char* g_get_user_name(void);
#endif
#endif