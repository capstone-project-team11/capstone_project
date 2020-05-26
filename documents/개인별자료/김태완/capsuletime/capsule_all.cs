using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class capsule_all : MonoBehaviour
{
    private float time;
    private int value;
    // Start is called before the first frame update
    void Start()
    {

    }

    // Update is called once per frame
    void Update()
    {
        //시간증가
        time += Time.deltaTime;
        //1초 동안 capsule의 반복적인 rotation 값을 변경하여 떨림 효과 연출
        if (time > 4 && time < 5)
        {
            rechange();
            rechange();
            rechange();
            rechange();
            rechange();
            rechange();
            rechange();
            rechange();
            rechange();
        }
        //capsule의 rotation을 초기화
        else if (time == 5)
        {
            transform.rotation = Quaternion.Euler(0, 0, 0);
        }
    }
    //랜덤한 값을 받아 capsule의 rotation을 변경
    void rechange()
    {
        value = Random.Range(-3, 3);
        transform.rotation = Quaternion.Euler(0, 0, value * Time.deltaTime * 20);
    }
}
