using System.Collections;
using System.Collections.Generic;
using UnityEngine.Networking;
using UnityEngine;
using UnityEngine.UI;

public class ReplyClick : MonoBehaviour
{
    public static string parentName;

    //댓글에 대한 댓글 등록
    public void replyClick()
    {
        GameObject.Find("Scroll View").transform.GetChild(4).gameObject.SetActive(false);
        GameObject.Find("Scroll View").transform.GetChild(5).gameObject.SetActive(true);

        parentName = transform.parent.transform.name;
        parentName = parentName.Replace("main","");
    }

    //대댓글에 대한 댓글 등록
    public void replyClick2()
    {
        GameObject.Find("Scroll View").transform.GetChild(4).gameObject.SetActive(false);
        GameObject.Find("Scroll View").transform.GetChild(5).gameObject.SetActive(true);

        parentName = transform.parent.transform.name;
        parentName = parentName.Replace("main", "");
        parentName = parentName.Substring(parentName.IndexOf("y") + 1);
    }
}
