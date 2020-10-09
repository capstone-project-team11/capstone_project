using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class CheckInputfield : MonoBehaviour
{
    public InputField target;
    private float target_y;
    private bool flag = false;

    private void Start()
    {
        target_y = target.transform.localPosition.y;
    }

    //댓글 입력시 위치 변화로 사용자에게 현재 입력된 댓글 확인
    void Update()
    {
        if (target.isFocused == true && flag == false)
        {
            flag = true;
            target.transform.localPosition = new Vector3(0, 100, 0);
        }
        else if(target.isFocused == false && flag == true)
        {
            flag = false;
            target.transform.localPosition = new Vector3(0, target_y, 0);
        }
    }
}
