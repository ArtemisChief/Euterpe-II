package pianoroll.component.renderer;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;
import glm.vec._2.Vec2;
import pianoroll.component.Pianoroll;
import pianoroll.entity.Background;
import pianoroll.util.Semantic;
import uno.glsl.Program;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.jogamp.opengl.GL.*;
import static uno.buffer.UtilKt.destroyBuffers;

public class BackgroundRenderer {

    private final Background piano;

    private final List<Background> columnList;
    private final List<Background> rowList;

    private final List<Background> unbindRowList;

    private IntBuffer buffer;

    public BackgroundRenderer() {
        piano=new Background(Semantic.Piano.KEY_MAX/2,0.0f);

        columnList = Pianoroll.GetInstance().getBackgroundController().getColumnList();
        rowList = Pianoroll.GetInstance().getBackgroundController().getRowList();

        unbindRowList =new ArrayList<>();
    }

    public void init(GL3 gl) {
        final float[] vertexDataColumn = {
                1.178f, 100.0f,          // Right-Top
                1.178f,   0.0f           // Right-Bottom
        };

        final float[] vertexDataRow = {
                -70.0f, 0.0f,            // Left-Bottom
                 70.0f, 0.0f             // Right-Bottom
        };

        final float[] vertexDataPiano = {
                -70.0f,   0.0f,          // Left-Top
                -70.0f, -13.0f,          // Left-Bottom
                 70.0f, -13.0f,          // Right-Bottom
                 70.0f,   0.0f           // Right-Top
        };

        buffer = GLBuffers.newDirectIntBuffer(3);

        FloatBuffer vertexBufferColumn = GLBuffers.newDirectFloatBuffer(vertexDataColumn);
        FloatBuffer vertexBufferRow = GLBuffers.newDirectFloatBuffer(vertexDataRow);
        FloatBuffer vertexBufferPiano = GLBuffers.newDirectFloatBuffer(vertexDataPiano);

        gl.glGenBuffers(3, buffer);

        gl.glBindBuffer(GL_ARRAY_BUFFER, buffer.get(Semantic.Buffer.VERTEX_COLUMN));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferColumn.capacity() * Float.BYTES, vertexBufferColumn, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, buffer.get(Semantic.Buffer.VERTEX_ROW));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferRow.capacity() * Float.BYTES, vertexBufferRow, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, buffer.get(Semantic.Buffer.VERTEX_PIANO));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferPiano.capacity() * Float.BYTES, vertexBufferPiano, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        destroyBuffers(vertexBufferColumn, vertexBufferRow, vertexBufferPiano);

        for (int trackID = 2; trackID < Semantic.Piano.KEY_MAX; trackID += 12) {
            Background column = new Background(trackID, 0.0f);

            gl.glGenVertexArrays(1, column.getVao());

            gl.glBindVertexArray(column.getVao().get(0));
            {
                gl.glBindBuffer(GL_ARRAY_BUFFER, buffer.get(Semantic.Buffer.VERTEX_COLUMN));
                {
                    gl.glEnableVertexAttribArray(Semantic.Attr.POSITION);
                    gl.glVertexAttribPointer(Semantic.Attr.POSITION, Vec2.length, GL_FLOAT, false, Vec2.SIZE, 0);
                }
                gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
            }
            gl.glBindVertexArray(0);

            columnList.add(column);
        }

        for (int trackID = 7; trackID < Semantic.Piano.KEY_MAX; trackID += 12) {
            Background column = new Background(trackID, 0.0f);

            gl.glGenVertexArrays(1, column.getVao());

            gl.glBindVertexArray(column.getVao().get(0));
            {
                gl.glBindBuffer(GL_ARRAY_BUFFER, buffer.get(Semantic.Buffer.VERTEX_COLUMN));
                {
                    gl.glEnableVertexAttribArray(Semantic.Attr.POSITION);
                    gl.glVertexAttribPointer(Semantic.Attr.POSITION, Vec2.length, GL_FLOAT, false, Vec2.SIZE, 0);
                }
                gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
            }
            gl.glBindVertexArray(0);

            columnList.add(column);
        }

        for (int i = 0; i < 2; i++) {
            Background row = new Background(Semantic.Piano.KEY_MAX / 2, i * 4 * Semantic.Pianoroll.LENGTH_PER_CROTCHET);

            gl.glGenVertexArrays(1, row.getVao());

            gl.glBindVertexArray(row.getVao().get(0));
            {
                gl.glBindBuffer(GL_ARRAY_BUFFER, buffer.get(Semantic.Buffer.VERTEX_ROW));
                {
                    gl.glEnableVertexAttribArray(Semantic.Attr.POSITION);
                    gl.glVertexAttribPointer(Semantic.Attr.POSITION, Vec2.length, GL_FLOAT, false, Vec2.SIZE, 0);
                }
                gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
            }
            gl.glBindVertexArray(0);

            rowList.add(row);
        }

        gl.glGenVertexArrays(1, piano.getVao());

        gl.glBindVertexArray(piano.getVao().get(0));
        {
            gl.glBindBuffer(GL_ARRAY_BUFFER, buffer.get(Semantic.Buffer.VERTEX_PIANO));
            {
                gl.glEnableVertexAttribArray(Semantic.Attr.POSITION);
                gl.glVertexAttribPointer(Semantic.Attr.POSITION, Vec2.length, GL_FLOAT, false, Vec2.SIZE, 0);
            }
            gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
        gl.glBindVertexArray(0);
    }

    public void addToUnbindRowList(Background row) {
        unbindRowList.add(row);
    }

    public void bindBuffer(GL3 gl) {
        if(!unbindRowList.isEmpty()) {
            Iterator<Background> iterator = unbindRowList.iterator();
            while (iterator.hasNext()) {
                Background row=iterator.next();

                // bind vao
                gl.glGenVertexArrays(1, row.getVao());

                gl.glBindVertexArray(row.getVao().get(0));
                {
                    gl.glBindBuffer(GL_ARRAY_BUFFER, buffer.get(Semantic.Buffer.VERTEX_ROW));
                    {
                        gl.glEnableVertexAttribArray(Semantic.Attr.POSITION);
                        gl.glVertexAttribPointer(Semantic.Attr.POSITION, Vec2.length, GL_FLOAT, false, Vec2.SIZE, 0);
                    }
                    gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
                }
                gl.glBindVertexArray(0);

                iterator.remove();
            }
        }
    }

    public void drawColumnRows(GL3 gl, Program program) {
        gl.glUseProgram(program.name);

        gl.glUniform1f(program.get("scaleY"), 1.0f);
        gl.glUniform1f(program.get("offsetY"), 0.0f);
        gl.glUniform1f(program.get("posZ"), 0.1f);
        gl.glUniform1i(program.get("colorID"), Semantic.Color.GREY);

        for (Background column : columnList) {
            gl.glBindVertexArray(column.getVao().get(0));
            gl.glUniform1i(program.get("trackID"), column.getTrackID());

            gl.glLineWidth(2.0f);
            gl.glDrawArrays(GL_LINE_STRIP, 0, 2);
        }

        for (Background row : rowList) {
            gl.glBindVertexArray(row.getVao().get(0));
            gl.glUniform1i(program.get("trackID"), row.getTrackID());
            gl.glUniform1f(program.get("offsetY"), row.getOffsetY());

            gl.glLineWidth(2.0f);
            gl.glDrawArrays(GL_LINE_STRIP, 0, 2);
        }

        gl.glBindVertexArray(piano.getVao().get(0));
        gl.glUniform1i(program.get("trackID"), piano.getTrackID());
        gl.glUniform1f(program.get("offsetY"), piano.getOffsetY());
        gl.glUniform1f(program.get("posZ"), 0.4f);
        gl.glDrawArrays(GL_TRIANGLE_FAN, 0, 4);

        gl.glBindVertexArray(0);
    }

    public void dispose(GL3 gl) {
        for (Background column : columnList)
            gl.glDeleteVertexArrays(1, column.getVao());

        for (Background row : rowList)
            gl.glDeleteVertexArrays(1, row.getVao());

        gl.glDeleteBuffers(3, buffer);
        destroyBuffers(buffer);
    }

}
