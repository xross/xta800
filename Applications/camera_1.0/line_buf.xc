/**
 * Module:  camera
 * Version: 1.0
 * Build:   N/A
 * File:    src/buffer/line_buf.xc
 *
 * The copyrights, all other intellectual and industrial 
 * property rights are retained by XMOS and/or its licensors. 
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2009
 **/                                   
#include "line_buf.h"

#include <print.h>

#define BUF_SIZE (320*2)

/*
 * Initialise the first buffer and the state variables
 */ 
{int, int, int, int} init( streaming chanend c_lcd_pro, short buf[3][BUF_SIZE])
{
	// fill first buffer
	for (int i = 0; i < BUF_SIZE; i++)
	{
		c_lcd_pro :> buf[0][i];		
	}
	
	// rd = buf 1; wr = buf 2; dirty = buf 3; available = 0;
	return {1,2,3,0};
}

/*
 * Handle the producer buffer ordering
 */
{int, int, int} producer_handler( int w, int a, int d)
{
	if (d != 0 )
	{
		int x;
		x = d;
		d = w;
		w = x;
	}
	else
	{
		d = w;
		w = a;
		a = 0;
	}
	
	return { w, a, d };
}

/*
 * Handle the consumer buffer ordering
 */
{ int, int, int } consumer_handler( int r, int a, int d)
{
	if (d != 0)
	{
		a = r;
		r = d;
		d = 0;
	}
	
	return { r, a, d };
}

/*
 * This function buffers up an image line at a time
 */ 
#pragma unsafe arrays
void line_buffer( streaming chanend c_lcd_pro, streaming chanend c_lcd_con )
{
	short buf[3][BUF_SIZE];	
	
	int read, write, available, dirty, done;
	unsigned pro_d_count, con_d_count;
	
	// initialise the system
	{read, write, available, dirty} = init( c_lcd_pro, buf);	
	done = 0;
	pro_d_count = 0;
	con_d_count = 0;

	while (done == 0)
	{
		select
		{
		case c_lcd_con :> int _:
			c_lcd_con <: (unsigned)buf[read-1][con_d_count];
			con_d_count++;
			if (con_d_count == BUF_SIZE)
			{
				//printstr("R,A,D = "); printint(read); printstr(","); printint(available); printstr(","); printintln(dirty);
				{read, available, dirty } = consumer_handler( read, available, dirty );
				con_d_count = 0;
			}			
			break;
		case c_lcd_pro :> int x:			
			buf[write-1][pro_d_count] = x;
			pro_d_count++;
			if (pro_d_count == BUF_SIZE)
			{
				//printstr("W,A,D = "); printint(write); printstr(","); printint(available); printstr(","); printintln(dirty);
				{write, available, dirty } = producer_handler( write, available, dirty );
				pro_d_count = 0;
			}
			break;
		}
	}

}

