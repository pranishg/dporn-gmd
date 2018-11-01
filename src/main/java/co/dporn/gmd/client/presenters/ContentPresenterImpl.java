package co.dporn.gmd.client.presenters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.client.GWT;

import co.dporn.gmd.client.app.AppControllerModel;
import co.dporn.gmd.client.views.BlogCardUi;
import co.dporn.gmd.shared.AccountInfo;
import co.dporn.gmd.shared.Post;
import co.dporn.gmd.shared.SortField;

public class ContentPresenterImpl implements ContentPresenter, ScheduledCommand {

	private ContentView view;
	private AppControllerModel model;

	public ContentPresenterImpl(AppControllerModel model, ContentView view) {
		this.view=view;
		this.model=model;
	}	

	@Override
	public void setView(ContentView view) {
		this.view=view;
	}

	private void loadInitialPosts() {
		CompletableFuture<List<Post>> futurePosts = model.listPosts(0, SortField.BY_DATE);
	}
	
	private void loadFeaturedBlogs() {
		model.listFeatured().thenAccept((f)->{
			GWT.log("Featured blogs.");
			getContentView().getFeatured().clear();
			List<BlogCardView> cards = new ArrayList<>();
			f.getAuthors().forEach((a)->{
				BlogCardView card = new BlogCardUi();
				card.setAuthorName(a);
				AccountInfo i = f.getInfoMap().get(a);
				if (i==null) {
					return;
				}
				card.setAuthorName(i.getDisplayName());
				String profileImage = i.getProfileImage();
				if (profileImage==null) {
					return;
//					profileImage=GWT.getHostPageBaseURL()+"images/avatarImagePlaceholder.png";
				} else {
					if (!profileImage.startsWith("https://steemitimages.com/")) {
						profileImage = "https://steemitimages.com/150x150/"+profileImage;
					}
				}
				card.setAvatarUrl(profileImage);
				card.setBlurb(i.getAbout());
				String coverImage = i.getCoverImage();
				if (coverImage==null) {
					return;
//					coverImage=GWT.getHostPageBaseURL()+"images/coverImagePlaceholder.png";
				} else {
					if (!coverImage.startsWith("https://steemitimages.com/")) {
						coverImage = "https://steemitimages.com/500x500/"+coverImage;
					}
				}
				card.setImageUrl(coverImage);
				cards.add(card);
			});
			cards.subList(0, Math.min(8, cards.size())).forEach((w)->getContentView().getFeatured().add(w.asWidget()));
		});
	}


	@Override
	public ContentView getContentView() {
		return view;
	}

	@Override
	public void setModel(AppControllerModel model) {
		this.model=model;
	}

	@Override
	public void execute() {
		GWT.log(this.getClass().getSimpleName()+"#execute");
		loadInitialPosts();
		loadFeaturedBlogs();		
	}
}
