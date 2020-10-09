using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class guideExit : MonoBehaviour
{
    //guide 창 비활성화
    public void guidePopupExit()
    {
        transform.parent.gameObject.SetActive(false);
    }
}
