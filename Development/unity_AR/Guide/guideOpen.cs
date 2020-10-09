using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class guideOpen : MonoBehaviour
{
    //guide 창 활성화
    public void guidelineOpen()
    {
        PlayerPrefs.SetInt("flag", 0);
        transform.parent.transform.parent.transform.GetChild(6).gameObject.SetActive(true);
    }
}
