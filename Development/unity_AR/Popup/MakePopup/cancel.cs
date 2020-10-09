using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class cancel : MonoBehaviour
{
    //임시 캡슐 생성 여부 확인창 종료
    public void exitMakePopup()
    {
        transform.parent.gameObject.SetActive(false);
    }
}
