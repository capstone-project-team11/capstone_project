using System.Collections;
using System.Collections.Generic;
using UnityEngine;

//팝업창 이미지, 비디오 전환
public class pop_next : MonoBehaviour
{
    //picture의 하위 오브젝트들의 순서 변경을 이용하여 전환

    public void next()
    {
        var obj = GameObject.Find("picture").transform;
        int c_num = obj.childCount;
        obj.GetChild(c_num-1).transform.SetAsFirstSibling();
    }

    public void pre()
    {
        var obj = GameObject.Find("picture").transform;
        int c_num = obj.childCount;
        obj.GetChild(0).transform.SetAsLastSibling();
    }
}
