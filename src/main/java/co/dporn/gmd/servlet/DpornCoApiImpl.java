package co.dporn.gmd.servlet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import co.dporn.gmd.shared.AccountInfo;
import co.dporn.gmd.shared.ActiveBlogsResponse;
import co.dporn.gmd.shared.DpornCoApi;
import co.dporn.gmd.shared.PingResponse;
import co.dporn.gmd.shared.Post;
import co.dporn.gmd.shared.PostListResponse;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("1.0")
public class DpornCoApiImpl implements DpornCoApi {

	@Override
	public PingResponse ping() {
		return new PingResponse(true);
	}

	@Override
	public PostListResponse posts(String startId, int count) {
		if (count < 1) {
			count = 1;
		}
		if (count > 50) {
			count = 50;
		}
		List<Post> posts = MongoDpornoCo.listPosts(startId, count);
		Set<String> accountNameList = new HashSet<>();
		posts.forEach(p -> accountNameList.add(p.getAuthor()));
		Map<String, AccountInfo> infoMap = SteemJInstance.getBlogDetails(accountNameList);
		PostListResponse response = new PostListResponse();
		response.setPosts(posts);
		response.setInfoMap(infoMap);
		return response;
	}

	@Override
	public PostListResponse posts(int count) {
		return posts("", count);
	}

	@Override
	public ActiveBlogsResponse blogsRecent() {
		List<String> active = SteemJInstance.getActiveNsfwVerifiedList();
		List<String> sublist = active.subList(0, Math.min(active.size(), 16));
		ActiveBlogsResponse activeBlogsResponse = new ActiveBlogsResponse(sublist);
		activeBlogsResponse.setInfoMap(SteemJInstance.getBlogDetails(sublist));
		return activeBlogsResponse;
	}
}
