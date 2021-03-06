package com.zeyuan.kyq.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.bumptech.glide.Glide;
import com.zeyuan.kyq.Entity.ForumBaseBean;
import com.zeyuan.kyq.Entity.ForumBaseEntity;
import com.zeyuan.kyq.Entity.Shortcut;
import com.zeyuan.kyq.R;
import com.zeyuan.kyq.adapter.CircleFindRecyclerAdapter;
import com.zeyuan.kyq.app.LazyFragment;
import com.zeyuan.kyq.bean.ShortcutBean;
import com.zeyuan.kyq.biz.Factory;
import com.zeyuan.kyq.biz.HttpResponseInterface;
import com.zeyuan.kyq.biz.manager.FunctionGuideManager;
import com.zeyuan.kyq.biz.manager.PointManager;
import com.zeyuan.kyq.utils.Const;
import com.zeyuan.kyq.utils.Contants;
import com.zeyuan.kyq.utils.ExceptionUtils;
import com.zeyuan.kyq.utils.UserinfoData;
import com.zeyuan.kyq.view.FocusFriendActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/12.
 * 关注的好友发布的帖子
 */

public class CircleFriendPostFragment extends LazyFragment implements HttpResponseInterface, View.OnClickListener {
    private int page = 0;
    private ProgressBar pd;
    private View body;
    private XRefreshView xRefreshView;
    //    private RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    private CircleFindRecyclerAdapter adapter;
    private List<ForumBaseEntity> forumListDatas;//装帖子列表的数据
    private boolean refresh = false;
    private boolean loading = false;
    private LinearLayout ll_empty;

    @Override
    protected void setDefaultFragmentTitle(String title) {

    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        rootView = inflater.inflate(R.layout.fragment_circle_friend_post, container, false);
//
//        initView();
//        initData();
//
//        return rootView;
//    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_circle_friend_post, container, false);

        initView();
        // initData();
        return rootView;
    }

    private void initView() {
        try {
            pd = (ProgressBar) findViewById(R.id.pd);
            body = findViewById(R.id.body);
            xRefreshView = (XRefreshView) findViewById(R.id.xrv);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
            ll_empty = (LinearLayout) findViewById(R.id.ll_empty);
            Button btn_focus = (Button) findViewById(R.id.btn_focus);

            recyclerView.setHasFixedSize(true);
            forumListDatas = new ArrayList<>();
            adapter = new CircleFindRecyclerAdapter(context, new ArrayList<Shortcut>(), forumListDatas);
            // 设置静默加载模式
//        xRefreshView1.setSilenceLoadMore();
            layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            // 静默加载模式不能设置footerview
            recyclerView.setAdapter(adapter);
            //设置刷新完成以后，headerview固定的时间
            xRefreshView.setPinnedTime(1000);
            xRefreshView.setPullLoadEnable(true);
            xRefreshView.setMoveForHorizontal(true);
            // xRefreshView.setHeadMoveLargestDistence(200);
//        xRefreshView1.setAutoLoadMore(true);
            adapter.setCustomLoadMoreView(new XRefreshViewFooter(context));

            xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

                @Override
                public void onRefresh() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refresh = true;
                            page = 0;
                            Factory.postPhp(CircleFriendPostFragment.this, Const.PCareArc);
                            Factory.postPhp(CircleFriendPostFragment.this, Const.PApi_getCircleShortcut);
                        }
                    }, 500);
                }

                @Override
                public void onLoadMore(boolean isSilence) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {

                            loading = true;
                            page++;
                            Factory.postPhp(CircleFriendPostFragment.this, Const.PCareArc);

                        }
                    }, 1000);
                }
            });

            xRefreshView.setStateCallback(new XRefreshView.XRefreshViewScrollStateChangedCallback() {
                                              @Override
                                              public void XRVScrollStateChangedCallback(int scrollState) {
                                                  try {
                                                      if (scrollState == 2) {
                                                          Glide.with(context).pauseRequests();
                                                      } else {
                                                          Glide.with(context).resumeRequests();
                                                      }
                                                  } catch (Exception e) {
                                                      ExceptionUtils.ExceptionSend(e, "scrollStatus");
                                                  }

                                              }
                                          }

            );
            btn_focus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), FocusFriendActivity.class));
                }
            });
        } catch (Exception e) {
            ExceptionUtils.ExceptionSend(e, "initView");
        }
    }

    protected void initData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Factory.postPhp(CircleFriendPostFragment.this, Const.PCareArc);
                Factory.postPhp(CircleFriendPostFragment.this, Const.PApi_getCircleShortcut);
            }
        }, 300);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public Map getParamInfo(int tag) {
        Map<String, String> map = new HashMap<>();
        if (tag == Const.PCareArc) {
            map.put(Contants.InfoID, UserinfoData.getInfoID(context));
            map.put("page", page + "");
            map.put("pagesize", "20");
        } else if (tag == Const.PApi_getCircleShortcut) {
            map.put(Contants.InfoID, UserinfoData.getInfoID(context));
        }
        return map;
    }

    @Override
    public byte[] getPostParams(int flag) {
        return new byte[0];
    }

    @Override
    public void toActivity(Object response, int flag) {
        showUI();
        if (flag == Const.PCareArc) {
            ForumBaseBean bean = (ForumBaseBean) response;
            if (Const.RESULT.equals(bean.getiResult())) {
                bindListView(bean);
                if (!getActivity().isFinishing()) {
                    FunctionGuideManager.getInstance().showFriendForumGuide(getActivity());
                }
            } else {
                overLoading(1);
            }
        } else if (flag == Const.PApi_getCircleShortcut) {
            ShortcutBean bean = (ShortcutBean) response;
            if (Const.RESULT.equals(bean.getiResult())) {
                Collections.sort(bean.getData2(), comparator);
                adapter.updateShortcut(bean.getData2());
            }
        }
    }

    //快速入口排序比较器
    Comparator<Shortcut> comparator = new Comparator<Shortcut>() {
        public int compare(Shortcut s1, Shortcut s2) {
            // 按权重排序(从大到小)
            if (s1.getPowerNum() != s2.getPowerNum()) {
                return -(s1.getPowerNum() - s2.getPowerNum());
            }
            return 0;

        }
    };

    @Override
    public void showLoading(int flag) {

    }

    @Override
    public void hideLoading(int flag) {

    }

    @Override
    public void showError(int flag) {
        showUI();
        if (flag == Const.PGetForumList_bank) {
            overLoading(1);
        }
    }

    private void bindListView(ForumBaseBean bean) {
        try {
            List<ForumBaseEntity> list = bean.getForumAllNum();
            if (page == 0) {
                forumListDatas.clear();
                PointManager.getInstance().setT2(context);
            }
            if (list != null) {
                forumListDatas.addAll(list);
                overLoading(0);
            } else {
                overLoading(2);
            }
            adapter.notifyDataSetChanged();//刷新list
            setEmptyView(forumListDatas.size());
        } catch (Exception e) {
            ExceptionUtils.ExceptionToUM(e, getActivity(), "CircleFindFragment");
        }
    }

    private void setEmptyView(int item_size) {
        if (item_size > 0) {
            ll_empty.setVisibility(View.GONE);
        } else {
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    private void overLoading(int tag) {
        if (tag == 0) {
            if (refresh) {
                xRefreshView.stopRefresh();
                xRefreshView.setLoadComplete(false);
            }
            if (loading) {
                xRefreshView.stopLoadMore();
            }
        } else if (tag == 1) {
            if (refresh) {
                xRefreshView.stopRefresh();
            }
            if (loading) {
                page--;
                xRefreshView.stopLoadMore();
            }
        } else if (tag == 2) {
            if (refresh) {
                xRefreshView.stopRefresh();
            }
            if (loading) {
                page--;
                xRefreshView.setLoadComplete(true);
            }
        }
        if (refresh) refresh = false;
        if (loading) loading = false;
    }

    private void showUI() {
        if (body.getVisibility() != View.VISIBLE) {
            try {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pd.setVisibility(View.GONE);
                        body.setVisibility(View.VISIBLE);
                    }
                }, 300);
            } catch (Exception e) {
                ExceptionUtils.ExceptionSend(e, "onRefresh");
            }
        }
    }
}
