using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

//
public class popup : MonoBehaviour
{
    //랜더 텍스쳐 활성화
    private void Start()
    {
        GameObject.Find("ARCamera").transform.GetChild(0).gameObject.SetActive(true);
    }

    //팝업창의 하위 오브젝트 name에 현재 선택된 캡슐 이름 입력
    public void insert_n(string hit_name)
    {
        transform.GetComponent<Text>().text = "by." + hit_name;
    }

    //팝업창의 하위 오브젝트 content에 현재 선택된 캡슐 이름을 추가
    public void insert_c(string hit_name)
    {
        transform.GetComponent<Text>().text = hit_name + "을 충북대학교" +
            "에 심어봤어요~~!! 날씨도 화창하고 너무 좋은데 코로나 때문에 놀러가지도 " +
            "못하고 진짜 이건 너무하네요ㅠㅠ";
    }
}
