// Copyright 2009 Fredrik Petrini
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
// documentation files (the "Software"), to deal in the Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
// and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
// THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
// TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

#include "sid_store_data_2.h"

void sid_store_core_2(chanend c)
{
	unsigned short tune;

	// Send number of tunes in this core
	c <: (char)sid_count_2;
	
	while(1)
	{
		unsigned int offset = 0;

		// Wait for tune request
		c :> tune;

		// Send size of selected tune
		c <: (unsigned short)sid_size_2[tune];

		// Calculate offset to the selected tune
		for(int i=0; i<tune; i++)
	    	offset += sid_size_2[i];

		// Send the tune
		for(int i=0; i<sid_size_2[tune]; i++)
	    	c <: (char)sid_data_2[offset+i];
	}
}
