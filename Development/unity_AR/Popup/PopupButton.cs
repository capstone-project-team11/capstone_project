using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System;

public class PopupButton : MonoBehaviour
{
    public GameObject lockcapsule;
    public GameObject nomalcapsule;

    //popup창 비활성화
    public void ClosePopup()
    {
        if(Comment.commentFlag != true)
        {
            var capsule = GameObject.Find(Popup.saveCapsuleNumber);
            var capsule_num = Popup.saveCapsuleNumber.ToString();
            var capsule_x = capsule.transform.position.x;
            var capsule_z = capsule.transform.position.z;
            var capsule_text = capsule.GetComponent<Text>().text;

            var images = GameObject.Find("images").transform;

            //생성된 이미지 삭제
            for (int i = 0; i < images.childCount; i++)
            {
                Destroy(GameObject.Find("image" + i));
            }

            //popup창 비활성화에 따른 기본 아이콘 기능 활성화
            Rendering.capsuleName.Remove(Convert.ToInt32(CapsuleClick.hitCapsule));
            GameObject.Find("img_component").transform.GetChild(0).gameObject.SetActive(false);
            GameObject.Find("icons_component").transform.GetChild(2).gameObject.SetActive(true);
            GameObject.Find("popup").SetActive(false);
            Popup.popupActiveFlag = false;
            Destroy(GameObject.Find(Popup.saveCapsuleNumber));
            Invoke("delay", 2);
            if(capsule_text != "")
            {
                var tmpcapsule = Instantiate(lockcapsule, new Vector3(capsule_x, 0, capsule_z), Quaternion.identity);
                tmpcapsule.name = capsule_num;
                tmpcapsule.GetComponent<Text>().text = capsule_text;
            }
            else
            {
                var tmpcapsule = Instantiate(nomalcapsule, new Vector3(capsule_x, 0, capsule_z), Quaternion.identity);
                tmpcapsule.name = capsule_num;
            }
        }
    }

    private void delay()
    {
        print("2초 딜레이");
    }

    //이미지 전환(다음)
    public void next()
    {
        if (Comment.commentFlag != true)
        {
            var obj = GameObject.Find("images").transform;
            int c_num = obj.childCount;
            obj.GetChild(c_num - 1).transform.SetAsFirstSibling();
        }
    }

    //이미지 전환(이전)
    public void pre()
    {
        if (Comment.commentFlag != true)
        {
            var obj = GameObject.Find("images").transform;
            int c_num = obj.childCount;
            obj.GetChild(0).transform.SetAsLastSibling();
        }
    }
}
