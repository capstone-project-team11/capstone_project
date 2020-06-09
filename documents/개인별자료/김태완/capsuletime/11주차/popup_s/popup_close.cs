using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

//팝업창 종료
public class popup_close : MonoBehaviour
{
    public void closepop()
    {
        string tmp_name = GameObject.Find("name").transform.GetComponent<Text>().text;//캡슐의 이름을 변수에 저장
        GameObject.Find("popup").transform.gameObject.SetActive(false);//팝업창 비활성화
        Destroy(GameObject.Find(tmp_name.Substring(3)));//'by.' 부분을 제거한 캡슐 이름으로 캡슐 제거
    }
}
