/*
 * C# / XNA  port of Bullet (c) 2011 Mark Neale <xexuxjy@hotmail.com>
 *
 * Bullet Continuous Collision Detection and Physics Library
 * Copyright (c) 2003-2008 Erwin Coumans  http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 *
 * Originally HeightfieldTerrainShape.java reused and modified into
 * GenericHeightfieldTerrainShape by Albin Hedman
 */

package com.bulletphysics.collision.shapes;

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

public class GenericHeightfieldTerrainShape extends ConcaveShape
{
	protected Heightfield m_heightfield;

	protected Vector3f m_localAabbMin;
	protected Vector3f m_localAabbMax;
	protected Vector3f m_localOrigin;
	protected Vector3f m_localScaling;

	protected boolean m_flipQuadEdges;
	protected boolean m_useDiamondSubdivision;

	protected int m_upAxis;


	public GenericHeightfieldTerrainShape(Heightfield heightfield, int upAxis, boolean flipQuadEdges)
	{
		initialize(heightfield, upAxis, flipQuadEdges);
	}

	protected void quantizeWithClamp(int[] output, Vector3f point, int isMax)
	{
		/// given input vector, return quantized version
		/**
		  This routine is basically determining the gridpoint indices for a given
		  input vector, answering the question: "which gridpoint is closest to the
		  provided point?".

		  "with clamp" means that we restrict the point to be in the heightfield's
		  axis-aligned bounding box.
		 */

		Vector3f clampedPoint = new Vector3f();
		clampedPoint.set(point.x, point.y, point.z);
		VectorUtil.setMax(clampedPoint, m_localAabbMin);
		VectorUtil.setMin(clampedPoint, m_localAabbMax);

		output[0] = getQuantized(clampedPoint.x);
		output[1] = getQuantized(clampedPoint.y);
		output[2] = getQuantized(clampedPoint.z);
	}

	public static int getQuantized(float x)
	{
		if (x < 0.0f)
		{
			return (int) (x - 0.5);
		}
		return (int) (x + 0.5);
	}

	protected void getVertex(int x, int y, Vector3f vertex)
	{
		float height = m_heightfield.getHeight(x, y);

		switch (m_upAxis)
		{
		case 0:
		{
			vertex.set(height - m_localOrigin.x, x, y);
			break;
		}
		case 1:
		{
			vertex.set(x, height - m_localOrigin.y, y);
			break;
		}
		case 2:
		{
			vertex.set(x, y, height - m_localOrigin.z);
			break;
		}
		default:
		{
			//need to get valid m_upAxis
			assert (false);
			vertex.set(0f, 0f, 0f);
			break;
		}
		}

		VectorUtil.mul(vertex, vertex, m_localScaling);
	}

	public BroadphaseNativeType getShapeType()
	{
		return BroadphaseNativeType.TERRAIN_SHAPE_PROXYTYPE;
	}

	/// protected initialization
	/**
	  Handles the work of constructors so that public constructors can be
	  backwards-compatible without a lot of copy/paste.
	 */
	protected void initialize(Heightfield heightfield, int upAxis, boolean flipQuadEdges)
	{
		// validation
		assert heightfield != null : "null heightfield data";
		assert upAxis >= 0 && upAxis < 3 : "bad upAxis--should be in range [0,2]";

		float width = heightfield.getWidth();
		float length = heightfield.getLength();

		float minHeight = heightfield.getMinHeight();
		float maxHeight = heightfield.getMaxHeight();

		// initialize member variables
		m_heightfield = heightfield;
		m_flipQuadEdges = flipQuadEdges;
		m_useDiamondSubdivision = false;
		m_upAxis = upAxis;

		m_localScaling = new Vector3f();
		m_localScaling.set(1f, 1f, 1f);

		m_localAabbMin = new Vector3f();
		m_localAabbMax = new Vector3f();
		switch (m_upAxis)
		{
			case 0:
			{
				m_localAabbMin.set(minHeight, -width / 2.0f, -length / 2.0f);
				m_localAabbMax.set(maxHeight, +width / 2.0f, +length / 2.0f);
				break;
			}
			case 1:
			{
				m_localAabbMin.set(-width / 2.0f, minHeight, -length / 2.0f);
				m_localAabbMax.set(+width / 2.0f, maxHeight, +length / 2.0f);
				break;
			}
			case 2:
			{
				m_localAabbMin.set(-width / 2.0f, -length / 2.0f, minHeight);
				m_localAabbMax.set(+width / 2.0f, +length / 2.0f, maxHeight);
				break;
			}
			default:
			{
				//need to get valid m_upAxis
				assert false : "Bad m_upAxis";
				break;
			}
		}


		// remember origin (defined as exact middle of aabb)
		m_localOrigin = new Vector3f();
		m_localOrigin.set(0, 0, 0);

		for (int i = 0; i < vertices.length; ++i)
		{
			vertices[i] = new Vector3f();
		}
	}

	public void setUseDiamondSubdivision(boolean useDiamondSubdivision)
	{
		m_useDiamondSubdivision = useDiamondSubdivision;
	}

	@Override
	public void getAabb(Transform trans, Vector3f aabbMin, Vector3f aabbMax)
	{
		Vector3f tmp = new Vector3f();

		Vector3f localHalfExtents = new Vector3f();
		localHalfExtents.sub(m_localAabbMax, m_localAabbMin);
		VectorUtil.mul(localHalfExtents,localHalfExtents,m_localScaling);
		//localHalfExtents.mul(localHalfExtents,m_localScaling);
		localHalfExtents.scale(0.5f);

		float minHeight = m_heightfield.getMinHeight();
		float maxHeight = m_heightfield.getMaxHeight();
		Vector3f localOrigin = new Vector3f();
		localOrigin.set(0f,0f,0f);
		VectorUtil.setCoord(localOrigin,m_upAxis,(minHeight + maxHeight)*0.5f );
		VectorUtil.mul(localOrigin,localOrigin,m_localScaling);
		
		Matrix3f abs_b = new Matrix3f(trans.basis);
		MatrixUtil.absolute(abs_b);

		Vector3f center = new Vector3f(trans.origin);
		Vector3f extent = new Vector3f();
		abs_b.getRow(0, tmp);
		extent.x = tmp.dot(localHalfExtents);
		abs_b.getRow(1, tmp);
		extent.y = tmp.dot(localHalfExtents);
		abs_b.getRow(2, tmp);
		extent.z = tmp.dot(localHalfExtents);

		Vector3f margin = new Vector3f();
		margin.set(getMargin(), getMargin(), getMargin());
		extent.add(margin);

		aabbMin.sub(center, extent);
		aabbMax.add(center, extent);
	}

	/// process all triangles within the provided axis-aligned bounding box
	/**
	  basic algorithm:
	    - convert input aabb to local coordinates (scale down and shift for local origin)
	    - convert input aabb to a range of heightfield grid points (quantize)
	    - iterate over all triangles in that subset of the grid
	 */
	//quantize the aabbMin and aabbMax, and adjust the start/end ranges
	int[] quantizedAabbMin = new int[3];
	int[] quantizedAabbMax = new int[3];
	Vector3f[] vertices = new Vector3f[3];

	public void checkNormal(Vector3f[] vertices1, TriangleCallback callback)
	{

		Vector3f tmp1 = new Vector3f();
		Vector3f tmp2 = new Vector3f();
		Vector3f normal = new Vector3f();

		tmp1.sub(vertices1[1], vertices1[0]);
		tmp2.sub(vertices1[2], vertices1[0]);

		normal.cross(tmp1, tmp2);
		normal.normalize();

	}

	public void processAllTriangles(TriangleCallback callback, Vector3f aabbMin, Vector3f aabbMax)
	{

		// scale down the input aabb's so they are in local (non-scaled) coordinates
		Vector3f invScale = new Vector3f();
		invScale.set(1f / m_localScaling.x, 1f / m_localScaling.y, 1f / m_localScaling.z);

		Vector3f localAabbMin = new Vector3f();
		Vector3f localAabbMax = new Vector3f();

		VectorUtil.mul(localAabbMin, aabbMin, invScale);
		VectorUtil.mul(localAabbMax, aabbMax, invScale);

		// account for local origin
		VectorUtil.add(localAabbMin, localAabbMin, m_localOrigin);
		VectorUtil.add(localAabbMax, localAabbMax, m_localOrigin);

		quantizeWithClamp(quantizedAabbMin, localAabbMin, 0);
		quantizeWithClamp(quantizedAabbMax, localAabbMax, 1);

		// expand the min/max quantized values
		// this is to catch the case where the input aabb falls between grid points!
		for (int i = 0; i < 3; ++i)
		{
			quantizedAabbMin[i]--;
			quantizedAabbMax[i]++;
		}

		int startX = 0;
		int endX = 0;
		int startJ = 0;
		int endJ = 0;

		float width = m_heightfield.getWidth();
		float length = m_heightfield.getLength();

		switch (m_upAxis)
		{
		case 0:
		{
			startX =(int)Math.max(-width / 2, quantizedAabbMin[1]);
			endX =  (int)Math.min(+width / 2, quantizedAabbMax[1]);
			startJ =(int)Math.max(-length / 2, quantizedAabbMin[2]);
			endJ =  (int)Math.min(+length / 2, quantizedAabbMax[2]);
			break;
		}
		case 1:
		{
			startX =(int)Math.max(-width / 2, quantizedAabbMin[0]);
			endX =  (int)Math.min(+width / 2, quantizedAabbMax[0]);
			startJ =(int)Math.max(-length / 2, quantizedAabbMin[2]);
			endJ =  (int)Math.min(+length / 2, quantizedAabbMax[2]);
			break;
		}
		case 2:
		{
			startX =(int)Math.max(-width / 2, quantizedAabbMin[0]);
			endX =  (int)Math.min(+width / 2, quantizedAabbMax[0]);
			startJ =(int)Math.max(-length / 2, quantizedAabbMin[1]);
			endJ =  (int)Math.min(+length / 2, quantizedAabbMax[1]);
			break;
		}
		default:
		{
			//need to get valid m_upAxis
			assert (false);
			break;
		}
		}

		// debug draw the boxes?
		for (int j = startJ; j < endJ; j++)
		{
			for (int x = startX; x < endX; x++)
			{
				if (m_flipQuadEdges || (m_useDiamondSubdivision && (((j + x) & 1) > 0)))
				{
					//first triangle
					getVertex(x, j, vertices[0]);
					getVertex(x + 1, j, vertices[1]);
					getVertex(x + 1, j + 1, vertices[2]);
					callback.processTriangle(vertices, x, j);
					//second triangle
					getVertex(x, j, vertices[0]);
					getVertex(x + 1, j + 1, vertices[1]);
					getVertex(x, j + 1, vertices[2]);

					callback.processTriangle(vertices, x, j);
				}
				else
				{
					//first triangle
					getVertex(x, j, vertices[0]);
					getVertex(x, j + 1, vertices[1]);
					getVertex(x + 1, j, vertices[2]);
					checkNormal(vertices, callback);
					callback.processTriangle(vertices, x, j);

					//second triangle
					getVertex(x + 1, j, vertices[0]);
					getVertex(x, j + 1, vertices[1]);
					getVertex(x + 1, j + 1, vertices[2]);
					checkNormal(vertices, callback);
					callback.processTriangle(vertices, x, j);

					//	                        getVertex(x, j, vertices[0]);
					//	                        getVertex(x+1, j, vertices[1]);
					//	                        getVertex(x + 1, j+1, vertices[2]);
					//	                        callback.processTriangle(vertices, x, j);
					//
					//	                        //second triangle
					//	                        getVertex(x , j, vertices[0]);
					//	                        getVertex(x+1, j + 1, vertices[1]);
					//	                        getVertex(x + 1, j + 1, vertices[2]);
					//	                        callback.processTriangle(vertices, x, j);

				}
			}
		}
	}

	public void calculateLocalInertia(float mass, Vector3f inertia)
	{
		//moving concave objects not supported
		inertia.set(0f, 0f, 0f);
	}

	public void setLocalScaling(Vector3f scaling)
	{
		m_localScaling.set(scaling);
	}

	public Vector3f getLocalScaling(Vector3f localScaling)
	{
		localScaling.set(m_localScaling);
		return localScaling;
	}

	//debugging
	public String getName()
	{
		return "HEIGHTFIELD";
	}
}
