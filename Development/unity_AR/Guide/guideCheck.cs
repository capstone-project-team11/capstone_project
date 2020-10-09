using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class guideCheck : MonoBehaviour
{
    //다시보지 않기 기능 고정
    public void Save()
    {
        PlayerPrefs.SetInt("flag", 1);
        transform.parent.gameObject.SetActive(false);
    }
}
