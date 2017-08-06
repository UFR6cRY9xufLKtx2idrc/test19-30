package me.ykrank.s1next.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.app.model.AppThread
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.util.MathUtil
import me.ykrank.s1next.view.fragment.AppPostListPagerFragment
import me.ykrank.s1next.view.fragment.PostListFragment
import me.ykrank.s1next.widget.WifiBroadcastReceiver

/**
 * An Activity which includes [android.support.v4.view.ViewPager]
 * to represent each page of post lists.
 */
class AppPostListActivity : BaseActivity(), AppPostListPagerFragment.PagerCallback, WifiBroadcastReceiver.NeedMonitorWifi {

    private lateinit var thread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_long_title)

        disableDrawerIndicator()

        thread = intent.getParcelableExtra<Thread>(ARG_THREAD)
        title = thread.title

        if (savedInstanceState == null) {
            val fragment: Fragment
            val pageNum = intent.getIntExtra(ARG_PAGE_NUM, 1)
            val postId = intent.getStringExtra(ARG_QUOTE_POST_ID)

            fragment = AppPostListPagerFragment.newInstance(thread.id, pageNum, postId)
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, fragment,
                    PostListFragment.TAG).commit()
        }
    }

    override fun getTotalPages(): Int {
        return MathUtil.divide(thread.reliesCount + 1, Api.POSTS_PER_PAGE)
    }

    override var threadInfo: AppThread? = null

    companion object {
        val RESULT_BLACKLIST = 11

        private val ARG_THREAD = "thread"
        private val ARG_PAGE_NUM = "page_num"
        private val ARG_QUOTE_POST_ID = "quote_post_id"

        fun start(context: Context, thread: Thread, pageNum: Int, postId: String) {
            val intent = Intent(context, AppPostListActivity::class.java)
            intent.putExtra(ARG_THREAD, thread)
            if (!TextUtils.isEmpty(postId)) {
                intent.putExtra(ARG_QUOTE_POST_ID, postId)
            }
            intent.putExtra(ARG_PAGE_NUM, pageNum)

            if (context is Activity)
                context.startActivityForResult(intent, RESULT_BLACKLIST)
            else
                context.startActivity(intent)
        }
    }
}
