#include "iAP.h"
#include "iPodEqualizer.h"
#include "safestring.h"

#ifdef DSP_ENABLED
typedef struct
{
  char namestring[20];
  int value;
} eqStruct;
extern int numEqs;
extern eqStruct eqNames[];
#else
int numEqs = 0;
#endif

int currenteq = 0;

/*
 * iPod_Equalizer_Rx
 * Call on receipt of equalizer command from iAP stack
 */
void iPod_Equalizer_Rx(streaming_chanend ciPodHID, int cmdVal, int rettransid, streaming chanend cAudioMixer[3])
{
  switch (cmdVal)
  {
    case CMD_equalizerLingo_GetCurrentEQIndex:
      iAP_equalizerLingo_RetCurrentEQIndex_client(ciPodHID, rettransid, currenteq);
      break;
    case CMD_equalizerLingo_SetCurrentEQIndex:
    {
      char eqindex;
      ciPodHID :> eqindex;
      if (eqindex < numEqs)
      {
        currenteq = eqindex;
#ifdef DSP_ENABLED
        cAudioMixer[1] <: eqNames[eqindex].value;
#endif
        iAP_equalizerLingo_Ack_client(ciPodHID, rettransid, 0, 0x03);
      }
      else
      {
        iAP_equalizerLingo_Ack_client(ciPodHID, rettransid, 1, 0x03);
      }
    }
    break;
    case CMD_equalizerLingo_GetEQSettingCount:
      iAP_equalizerLingo_RetEQSettingCount_client(ciPodHID, rettransid, numEqs);
      break;
    case CMD_equalizerLingo_GetEQIndexName:
    {
      char eqindex;
      ciPodHID :> eqindex;
      if (eqindex < numEqs)
      {
#ifdef DSP_ENABLED
        iAP_equalizerLingo_RetEQIndexName_client(ciPodHID, rettransid, eqindex, safestrlen(eqNames[eqindex].namestring) + 1, eqNames[eqindex].namestring);
#endif
      }
    }
    break;
  }
  
}

