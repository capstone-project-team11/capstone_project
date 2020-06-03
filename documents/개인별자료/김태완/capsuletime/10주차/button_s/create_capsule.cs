using System.Collections;
using System.Collections.Generic;
using UnityEngine;


//캡슐 생성 클래스
public class create_capsule : MonoBehaviour
{
    //prefab을 추가할 Gameobject 선언
    public GameObject capsule;

    public void newobject()
    {
        //capsule의 동적 생성 위치를 Canvas 하위로 배정한 뒤 명칭을 test_capsule로 수정
        var c_parent = GameObject.Find("Canvas").transform;
        var c_child = Instantiate(capsule);
        c_child.transform.parent = c_parent;
        c_child.transform.localPosition = new Vector3(0, 0, -100);
        GameObject.Find("capsule(Clone)").name = "test_capsule";
    }
}