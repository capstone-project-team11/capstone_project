using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class exitExpire : MonoBehaviour
{
    //잠금만료 캡슐 팝업창 종료
    public void exitExpirePopup()
    {
        var count = GameObject.Find("Content2").transform.childCount;

        for (int i = 0; i < count; i++)
        {
            Destroy(GameObject.Find("key" + i));
        }

        transform.parent.gameObject.SetActive(false);
        KeyList.flag = false;
        KeyList.keyPppupFlag = false;
    }
}
