void _done(void)
{
	void (*fp)(void) = (void(*)(void))0xffffc000;
	
	(*fp)();
}
