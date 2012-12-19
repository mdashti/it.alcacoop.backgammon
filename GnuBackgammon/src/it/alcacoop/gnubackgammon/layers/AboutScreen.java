package it.alcacoop.gnubackgammon.layers;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM.Events;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class AboutScreen implements Screen {

  private Stage stage;
  private Image bgImg;
  private Table table;
  private Table tableContent;
  
  public AboutScreen(){
    TextureRegion  bgRegion = GnuBackgammon.atlas.findRegion("bg");
    bgImg = new Image(bgRegion);
    
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.resolution[0], GnuBackgammon.resolution[1], false);
    
    stage.addActor(bgImg);   
    
    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if(Gdx.input.isKeyPressed(Keys.BACK)||Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED, "BACK");
        }
        return super.keyDown(event, keycode);
      }
    });
    
    final String gnuBgLink = "http://www.gnubg.org";
    final String gplLink = "http://www.gnu.org/licenses/gpl.html";
    final String githubLink1 = "https://github.com/alcacoop/it.alcacoop.gnubackgammon";
    final String githubLink2 = "https://github.com/alcacoop/libgnubg-android";
    final String wikipediaLink = "http://en.wikipedia.org/wiki/Backgammon#Rules";
    
    Label titleLabel = new Label("ABOUT", GnuBackgammon.skin);
    
    Label label1 = new Label("GnuBackgammon Mobile is based on GNU Backgammon (gnubg)", GnuBackgammon.skin);
    Label label1Link = new Label(gnuBgLink, GnuBackgammon.skin);
    label1Link.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.myRequestHandler.openURL(gnuBgLink);
      };
    });
    
    Label label2 = new Label("Its source code is released under a GPLv3 License", GnuBackgammon.skin);
    Label label2Link = new Label(gplLink, GnuBackgammon.skin);
    label2Link.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.myRequestHandler.openURL(gplLink);
      };
    });
    
    Label label3 = new Label("and is available on GitHub at:", GnuBackgammon.skin);
    Label label3Link = new Label(githubLink1, GnuBackgammon.skin);
    label3Link.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.myRequestHandler.openURL(githubLink1);
      };
    });
    Label label3Link2 = new Label(githubLink2, GnuBackgammon.skin);
    label3Link2.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.myRequestHandler.openURL(githubLink2);
      };
    });
    
    Label label4 = new Label("You can find a detailed description of game rules on Wikipedia at:", GnuBackgammon.skin);
    Label label4Link = new Label(wikipediaLink, GnuBackgammon.skin);
    label4Link.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.myRequestHandler.openURL(wikipediaLink);
      };
    });

    Label label5 = new Label("If you enjoy our game support us rating on the Play Store", GnuBackgammon.skin);
    Label label6 = new Label("or buying the ad-free version.", GnuBackgammon.skin);
   
    table = new Table();
    table.setWidth(stage.getWidth()*0.95f);
    table.setHeight(stage.getHeight()*0.9f);
    table.setX((stage.getWidth() - table.getWidth()) / 2);
    table.setY((stage.getHeight() - table.getHeight()) / 2);

    float width = table.getWidth()/12;
    float height = table.getWidth()/15;
    
    tableContent = new Table();
    tableContent.row();
    tableContent.add(label1).expand();
    tableContent.row();
    tableContent.add(label1Link).expand();
    tableContent.row();
    tableContent.add(label5).expand();
    tableContent.row();
    tableContent.add(label6).expand();
    tableContent.row();
    tableContent.add().expand().height(height/2);
    tableContent.row();
    tableContent.add(label2).expand();
    tableContent.row();
    tableContent.add(label2Link).expand();
    tableContent.row();
    tableContent.add().expand().height(height/2);
    tableContent.row();
    tableContent.add(label3).expand();
    tableContent.row();
    tableContent.add(label3Link).expand();
    tableContent.row();
    tableContent.add(label3Link2).expand();
    tableContent.row();
    tableContent.add().expand().height(height/2);
    tableContent.row();
    tableContent.add(label4).expand();
    tableContent.row();
    tableContent.add(label4Link).expand();
    tableContent.row();
    tableContent.add().expand().height(height/2);

    ScrollPane sc = new ScrollPane(tableContent,GnuBackgammon.skin2);
    sc.setFadeScrollBars(false);    

    table.row().pad(2);
    table.add(titleLabel).height(height).expand();
    table.row();
    table.add(sc).height(height*6).expand().fill();
    
    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED,((TextButton)event.getListenerActor()).getText().toString().toUpperCase());
      };
    };
    TextButton back = new TextButton("BACK", GnuBackgammon.skin2);
    back.addListener(cl);
    

    table.row();
    table.add(back).expand().fill().colspan(5).height(height).width(4*width);

    stage.addActor(table);
  }


  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 0);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }


  @Override
  public void resize(int width, int height) {
    bgImg.setWidth(stage.getWidth());
    bgImg.setHeight(stage.getHeight());
  }

  
  @Override
  public void show() {
    bgImg.setWidth(stage.getWidth());
    bgImg.setHeight(stage.getHeight());
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
    table.setColor(1,1,1,0);
    table.addAction(Actions.sequence(Actions.delay(0.1f),Actions.fadeIn(0.6f)));
  }

  @Override
  public void hide() {
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void dispose() {
  }
}
