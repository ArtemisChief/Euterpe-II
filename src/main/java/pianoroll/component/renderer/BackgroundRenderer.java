package pianoroll.component.renderer;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;
import glm.vec._2.Vec2;
import pianoroll.component.Pianoroll;
import pianoroll.entity.ColumnRow;
import pianoroll.util.Semantic;
import uno.glsl.Program;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static com.jogamp.opengl.GL.*;
import static uno.buffer.UtilKt.destroyBuffers;

public class BackgroundRenderer {

    private final List<ColumnRow> columnList;
    private final List<ColumnRow> rowList;

    public BackgroundRenderer() {
        this.columnList = Pianoroll.GetInstance().getBackgroundController().getColumnList();
        this.rowList = Pianoroll.GetInstance().getBackgroundController().getRowList();
    }

    public void init(GL3 gl) {
        final float[] vertexDataColumn = {
                1.178f, 100.0f,          // Right-Top
                1.178f, 0.0f           // Right-Bottom
        };

        final float[] vertexDataRow = {
                -70.0f, 0.0f,            // Left-Bottom
                70.0f, 0.0f             // Right-Bottom
        };

        IntBuffer buffer = GLBuffers.newDirectIntBuffer(2);

        FloatBuffer vertexBufferColumn = GLBuffers.newDirectFloatBuffer(vertexDataColumn);
        FloatBuffer vertexBufferRow = GLBuffers.newDirectFloatBuffer(vertexDataRow);

        gl.glGenBuffers(2, buffer);

        gl.glBindBuffer(GL_ARRAY_BUFFER, buffer.get(Semantic.Buffer.VERTEX_COLUMN));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferColumn.capacity() * Float.BYTES, vertexBufferColumn, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, buffer.get(Semantic.Buffer.VERTEX_ROW));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferRow.capacity() * Float.BYTES, vertexBufferRow, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        destroyBuffers(vertexBufferColumn, vertexBufferRow);

        for (int trackID = 2; trackID < 88; trackID += 12) {
            ColumnRow column = new ColumnRow(trackID);

            column.setVbo(buffer.get(Semantic.Buffer.VERTEX_COLUMN));
            columnList.add(column);
        }

        for (int trackID = 7; trackID < 88; trackID += 12) {
            ColumnRow column = new ColumnRow(trackID);

            column.setVbo(buffer.get(Semantic.Buffer.VERTEX_COLUMN));
            columnList.add(column);
        }

        for (int i = 0; i < 2; i++) {
            ColumnRow row = new ColumnRow(44);

            row.setVbo(buffer.get(Semantic.Buffer.VERTEX_ROW));
            row.setOffsetY(i * 4 * Semantic.Pianoroll.LENGTH_PER_CROTCHET);
            rowList.add(row);
        }

        for (ColumnRow column : columnList) {
            gl.glGenVertexArrays(1, column.getVao());

            gl.glBindVertexArray(column.getVao().get(0));
            {
                gl.glBindBuffer(GL_ARRAY_BUFFER, column.getVbo());
                {
                    gl.glEnableVertexAttribArray(Semantic.Attr.POSITION);
                    gl.glVertexAttribPointer(Semantic.Attr.POSITION, Vec2.length, GL_FLOAT, false, Vec2.SIZE, 0);
                }
                gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
            }
            gl.glBindVertexArray(0);
        }

        for (ColumnRow row : rowList) {
            gl.glGenVertexArrays(1, row.getVao());

            gl.glBindVertexArray(row.getVao().get(0));
            {
                gl.glBindBuffer(GL_ARRAY_BUFFER, row.getVbo());
                {
                    gl.glEnableVertexAttribArray(Semantic.Attr.POSITION);
                    gl.glVertexAttribPointer(Semantic.Attr.POSITION, Vec2.length, GL_FLOAT, false, Vec2.SIZE, 0);
                }
                gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
            }
            gl.glBindVertexArray(0);
        }

        gl.glDeleteBuffers(2, buffer);
        destroyBuffers(buffer);
    }

    public void drawColumnRows(GL3 gl, Program program) {
        gl.glUseProgram(program.name);

        gl.glUniform1f(program.get("scaleY"), 1f);
        gl.glUniform1f(program.get("offsetY"), 0);
        gl.glUniform1i(program.get("colorID"), 202);

        for (ColumnRow column : columnList) {
            gl.glBindVertexArray(column.getVao().get(0));
            gl.glUniform1i(program.get("trackID"), column.getTrackID());

            gl.glLineWidth(2.0f);
            gl.glDrawArrays(GL_LINE_STRIP, 0, 2);
        }

        for (ColumnRow row : rowList) {
            gl.glBindVertexArray(row.getVao().get(0));
            gl.glUniform1i(program.get("trackID"), row.getTrackID());
            gl.glUniform1f(program.get("offsetY"), row.getOffsetY());

            gl.glLineWidth(2.0f);
            gl.glDrawArrays(GL_LINE_STRIP, 0, 2);
        }

        gl.glBindVertexArray(0);
    }

}
