using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class expireOpen : MonoBehaviour
{
    public static bool expireFlag = false;

    //잠금 캡슐 애니메이션 효과
    public void clickExpireOpen()
    {
        GameObject.Find("exitExpirePopup").GetComponent<exitExpire>().exitExpirePopup();
        GameObject.Find(KeyList.saveNumber).GetComponent<b_active>().enabled = false;
        GameObject.Find(KeyList.saveNumber).transform.GetChild(2).transform.GetChild(0).GetComponent<unlockCapsule>().enabled = true;
        Invoke("shakeFunc",3);
        CapsuleClick.hitCapsule = KeyList.saveNumber;
    }

    private void shakeFunc()
    {
        GameObject.Find(KeyList.saveNumber).GetComponent<shake>().enabled = true;
    }
}
