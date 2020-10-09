using System.Collections;
using System.Collections.Generic;
using UnityEngine;

//생성된 캡슐이 기본적으로 돌고있는 모션을 취함
public class b_active : MonoBehaviour
{
    public float speed;
    void Update()
    {
        transform.Rotate(new Vector3(0, speed, 0));
    }
}