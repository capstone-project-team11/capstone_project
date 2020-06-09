using System.Collections;
using System.Collections.Generic;
using UnityEngine;

//캡슐이 흔들림 모션을 취함
public class shake : MonoBehaviour
{
    private int value;
    private float time;

    void Update()
    {
        time += Time.deltaTime;
        if (time < 1.5)
        {
            rechange();
        }
        else
        {
            transform.rotation = Quaternion.Euler(0, 0, 0);
            gameObject.GetComponent<shake>().enabled = false;
        }
    }
    void rechange()
    {
        value = Random.Range(-10, 10);
        transform.rotation = Quaternion.Euler(0, 0, value);
    }
}