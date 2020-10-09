using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class unlockCapsule : MonoBehaviour
{
    public float speed;
    private float time;

    //자물쇠 애니메이션 실행
    private void Update()
    {
        time += Time.deltaTime;

        if(time < 2)
        {
            transform.Rotate(new Vector3(0, 0, -speed));
        }
        else
        {
            transform.parent.gameObject.SetActive(false);
        }
    }
}
