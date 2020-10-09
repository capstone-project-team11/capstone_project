using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.IO;
using System.Net;
using UnityEngine.Networking;
using Newtonsoft.Json.Linq;
using System;

public class KeyList : MonoBehaviour
{
    public GameObject keyState;
    public static bool flag = false;
    public static string saveNumber = "none";
    public static bool keyPppupFlag = false;
    public GameObject Content2;

    private void Update()
    {
        if(flag == false)
        {
            flag = true;
            if(CapsuleClick.hitCapsule != "-1")
            {
                saveNumber = CapsuleClick.hitCapsule;
            }
            keyPppupFlag = true;
            StartCoroutine(showKeyList());
        }
    }

    //적용된 키 상태 표시
    IEnumerator showKeyList()
    {
        WWWForm form = new WWWForm();
        form.AddField("capsule_id", CapsuleClick.hitCapsule);
        form.AddField("nick_name", InformationData.userNickName);

        UnityWebRequest www = UnityWebRequest.Post(InformationData.serverUrl + "capsules/id", form);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            //캡슐의 전체 키의 개수와 적용된 키의 개수를 호출
            var tmp = www.downloadHandler.text;
            JObject capsuleData = JObject.Parse(tmp);
            var keyCount = Convert.ToInt32(capsuleData["key_count"]);
            var applyKey = Convert.ToInt32(capsuleData["used_key_count"]);
            var memberKey = capsuleData["members"].ToString();
            memberKey = memberKey.Substring(1, memberKey.Length - 2);
            memberKey = memberKey.Replace("},","*");
            var memberKeyList = memberKey.Split('*');
            
            for(int j = 0; j < memberKeyList.Length; j++)
            {
                if(j != memberKeyList.Length - 1)
                {
                    memberKeyList[j] += "}";
                }
            }

            //사용자가 키를 적용했는지 확인
            var keyFlag = false;

            //키를 적용한 멤버의 수만큼 적용 이미지로 교체
            for(int i=0;i< keyCount; i++)
            {
                var key = Instantiate(keyState);
                JObject memberStatus = JObject.Parse(memberKeyList[i]);

                if(memberStatus["status_key"].ToString() != "1")
                {
                    key.transform.GetChild(0).GetComponent<RawImage>().texture = Resources.Load<Texture>("Image/unlocked");
                }

                key.transform.GetChild(0).transform.GetChild(0).GetComponent<Text>().text = memberStatus["nick_name"].ToString();
                if (memberStatus["nick_name"].ToString() == InformationData.userNickName && memberStatus["status_key"].ToString() == "0")
                {
                    keyFlag = true;
                }
                key.transform.SetParent(GameObject.Find("Content2").transform);
                key.name = "key" + i;
            }

            //사용자가 키를 이미 적용한 상태면 키 추가 버튼 비활성화
            if (keyFlag == true)
            {
                GameObject.Find("expired").transform.GetChild(1).gameObject.SetActive(false);
                GameObject.Find("expired").transform.GetChild(2).gameObject.SetActive(true);
            }

            //키가 전부 적용된 경우 open 버튼 활성화
            if (applyKey == keyCount)
            {
                GameObject.Find("expired").transform.GetChild(1).gameObject.SetActive(false);
                GameObject.Find("expired").transform.GetChild(2).gameObject.SetActive(false);
                GameObject.Find("expired").transform.GetChild(3).gameObject.SetActive(true);
            }

            if(keyCount > 3)
            {
                Content2.transform.localScale = new Vector3(1 + (float)(0.1 * keyCount), 1, 1);
            }
        }
    }
}
