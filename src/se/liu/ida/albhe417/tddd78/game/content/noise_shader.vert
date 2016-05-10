/* sdnoise1234, Simplex noise with true analytic
 * derivative in 1D to 4D.
 *
 * Copyright © 2003-2012, Stefan Gustavson
 *
 * Contact: stefan.gustavson@gmail.com
 *
 * This library is public domain software, released by the author
 * into the public domain in February 2011. You may do anything
 * you like with it. You may even remove all attributions,
 * but of course I'd appreciate it if you kept my name somewhere.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 */

/*
 * This is an implementation of Perlin "simplex noise" over one
 * dimension (x), two dimensions (x,y), three dimensions (x,y,z)
 * and four dimensions (x,y,z,w). The analytic derivative is
 * returned, to make it possible to do lots of fun stuff like
 * flow animations, curl noise, analytic antialiasing and such.
 *
 * Visually, this noise is exactly the same as the plain version of
 * simplex noise provided in the file "snoise1234.c". It just returns
 * all partial derivatives in addition to the scalar noise value.
 *
 * 2012-01-12: Slight update to compile with MSVC (declarations moved).
 */



 #version 130
 in vec3 position;
 in vec3 normal;
 in vec3 color;

 out vec3 vertexColor;
 out vec3 vertexNormal;

 uniform mat4 modelViewProjectionMatrix;
 uniform mat4 modelMatrix;

/* Static data ---------------------- */

/*
 * Permutation table. This is just a random jumble of all numbers 0-255,
 * repeated twice to avoid wrapping the index at 255 for each lookup.
 */
const int perm[512] = int[](151,160,137,91,90,15,
  131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
  190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
  88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
  77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
  102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
  135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
  5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
  223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
  129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
  251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
  49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
  138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180,
  151,160,137,91,90,15,
  131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
  190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
  88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
  77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
  102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
  135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
  5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
  223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
  129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
  251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
  49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
  138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180
);

/*
 * Gradient tables. These could be programmed the Ken Perlin way with
 * some clever bit-twiddling, but this is more clear, and not really slower.
 */
vec2 grad2lut[8] = vec2[8](
  vec2( -1.0f, -1.0f ), vec2( 1.0f, 0.0f ), vec2( -1.0f, 0.0f ), vec2( 1.0f, 1.0f ),
  vec2( -1.0f, 1.0f ), vec2( 0.0f, -1.0f ), vec2( 0.0f, 1.0f ), vec2( 1.0f, -1.0f )
);



/* --------------------------------------------------------------------- */

/*
 * Helper functions to compute gradients in 1D to 4D
 * and gradients-dot-residualvectors in 2D to 4D.
 */

void grad2( int hash, out float gx, out float gy ) {
    int h = hash & 7;
    gx = grad2lut[h].x;
    gy = grad2lut[h].y;
    return;
}


/* Skewing factors for 2D simplex grid:
 * F2 = 0.5*(sqrt(3.0)-1.0)
 * G2 = (3.0-Math.sqrt(3.0))/6.0
 */
#define F2 .366025403f
#define G2 .211324865f

/** 2D simplex noise with derivatives.
 * If the last two arguments are not null, the analytic derivative
 * (the 2D gradient of the scalar noise field) is also calculated.
 */
float sdnoise2( float x, float y, out vec2 normal )
{
    float n0, n1, n2; /* Noise contributions from the three simplex corners */
    float gx0, gy0, gx1, gy1, gx2, gy2; /* Gradients at simplex corners */
    float t0, t1, t2, x1, x2, y1, y2;
    float t20, t40, t21, t41, t22, t42;
    float temp0, temp1, temp2, noise;

    /* Skew the input space to determine which simplex cell we're in */
    float s = ( x + y ) * F2; /* Hairy factor for 2D */
    float xs = x + s;
    float ys = y + s;
    int ii, i = int(floor( xs ));
    int jj, j = int(floor( ys ));

    float t = float(( i + j )) * G2;
    float X0 = i - t; /* Unskew the cell origin back to (x,y) space */
    float Y0 = j - t;
    float x0 = x - X0; /* The x,y distances from the cell origin */
    float y0 = y - Y0;

    /* For the 2D case, the simplex shape is an equilateral triangle.
     * Determine which simplex we are in. */
    int i1, j1; /* Offsets for second (middle) corner of simplex in (i,j) coords */
    if( x0 > y0 ) { i1 = 1; j1 = 0; } /* lower triangle, XY order: (0,0)->(1,0)->(1,1) */
    else { i1 = 0; j1 = 1; }      /* upper triangle, YX order: (0,0)->(0,1)->(1,1) */

    /* A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
     * a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
     * c = (3-sqrt(3))/6   */
    x1 = x0 - i1 + G2; /* Offsets for middle corner in (x,y) unskewed coords */
    y1 = y0 - j1 + G2;
    x2 = x0 - 1.0f + 2.0f * G2; /* Offsets for last corner in (x,y) unskewed coords */
    y2 = y0 - 1.0f + 2.0f * G2;

    /* Wrap the integer indices at 256, to avoid indexing perm[] out of bounds */
    ii = i % 256;
    jj = j % 256;

    /* Calculate the contribution from the three corners */
    t0 = 0.5f - x0 * x0 - y0 * y0;
    if( t0 < 0.0f ) t40 = t20 = t0 = n0 = gx0 = gy0 = 0.0f; /* No influence */
    else {
      grad2( perm[ii + perm[jj]], gx0, gy0 );
      t20 = t0 * t0;
      t40 = t20 * t20;
      n0 = t40 * ( gx0 * x0 + gy0 * y0 );
    }

    t1 = 0.5f - x1 * x1 - y1 * y1;
    if( t1 < 0.0f ) t21 = t41 = t1 = n1 = gx1 = gy1 = 0.0f; /* No influence */
    else {
      grad2( perm[ii + i1 + perm[jj + j1]], gx1, gy1 );
      t21 = t1 * t1;
      t41 = t21 * t21;
      n1 = t41 * ( gx1 * x1 + gy1 * y1 );
    }

    t2 = 0.5f - x2 * x2 - y2 * y2;
    if( t2 < 0.0f ) t42 = t22 = t2 = n2 = gx2 = gy2 = 0.0f; /* No influence */
    else {
      grad2( perm[ii + 1 + perm[jj + 1]], gx2, gy2 );
      t22 = t2 * t2;
      t42 = t22 * t22;
      n2 = t42 * ( gx2 * x2 + gy2 * y2 );
    }

    /* Add contributions from each corner to get the final noise value.
     * The result is scaled to return values in the interval [-1,1]. */
    noise = 40.0f * ( n0 + n1 + n2 );

    /* Compute derivative, if requested by supplying non-null pointers
     * for the last two arguments
	 *  A straight, unoptimised calculation would be like:
     *    normal.x = -8.0f * t20 * t0 * x0 * ( gx0 * x0 + gy0 * y0 ) + t40 * gx0;
     *    normal.y = -8.0f * t20 * t0 * y0 * ( gx0 * x0 + gy0 * y0 ) + t40 * gy0;
     *    normal.x += -8.0f * t21 * t1 * x1 * ( gx1 * x1 + gy1 * y1 ) + t41 * gx1;
     *    normal.y += -8.0f * t21 * t1 * y1 * ( gx1 * x1 + gy1 * y1 ) + t41 * gy1;
     *    normal.x += -8.0f * t22 * t2 * x2 * ( gx2 * x2 + gy2 * y2 ) + t42 * gx2;
     *    normal.y += -8.0f * t22 * t2 * y2 * ( gx2 * x2 + gy2 * y2 ) + t42 * gy2;
	 */
    temp0 = t20 * t0 * ( gx0* x0 + gy0 * y0 );
    normal.x = temp0 * x0;
    normal.y = temp0 * y0;
    temp1 = t21 * t1 * ( gx1 * x1 + gy1 * y1 );
    normal.x += temp1 * x1;
    normal.y += temp1 * y1;
    temp2 = t22 * t2 * ( gx2* x2 + gy2 * y2 );
    normal.x += temp2 * x2;       normal.y += temp2 * y2;
    normal.x *= -8.0f;
    normal.y *= -8.0f;
    normal.x += t40 * gx0 + t41 * gx1 + t42 * gx2;
    normal.y += t40 * gy0 + t41 * gy1 + t42 * gy2;
    normal.x *= 40.0f; /* Scale derivative to match the noise scaling */
    normal.y *= 40.0f;

    return noise;
  }

vec3 calculateColor(float height){
    const float hexToFloat = 1.0 / 256.0;

    const vec3 snow = vec3(0xEE, 0xEE, 0xEE) * hexToFloat;
    const vec3 rock = vec3(0x88, 0x88, 0x55) * hexToFloat;
    const vec3 grass = vec3(0x00, 0x77, 0x00) * hexToFloat;
    const vec3 water = vec3(0x00, 0x00, 0x77) * hexToFloat;

    const float snowRockLine = 75;
    const float rockGrassLine = 30;
    const float grassWaterLine = 4;

    const int levelsOfNoise = 16;

    const float maxHeight = 1 << (levelsOfNoise + 1) - 1;
    const float minHeight = -maxHeight;

    const float deltaIn = maxHeight - minHeight;

    const float scale = 100 / deltaIn;

    float displacedValue = height - minHeight;

    float percentOfMaxHeight = displacedValue * scale;

    if(percentOfMaxHeight > snowRockLine)
        return snow;
    else if(percentOfMaxHeight > rockGrassLine)
        return rock;
    else if(percentOfMaxHeight > grassWaterLine)
        return grass;
    else
        return water;
  }

  void getTerrainValues(vec3 position, out vec3 positionOut, out vec3 normalOut, out vec3 colorOut){
  	float heightFactor = 0.05;
  	int levelsOfNoise = 16;
  	float height = 0;

  	float x = position.x;
  	float y = position.z;

  	int maxAmplitude = 1 << levelsOfNoise;

  	for (int amplitude = 1; amplitude <= maxAmplitude; amplitude <<= 1) {
  		float period = 1.0 / amplitude;
  		vec2 normal;
  		height += amplitude * sdnoise2(x * period, y * period, normal);
  		normalOut.xz += normal;
    }

    positionOut.x = position.x;
  	positionOut.y = height * heightFactor;
  	positionOut.z = position.z;

    normalOut.y = 1;

  	colorOut = calculateColor(height * heightFactor);
  }

  void main(){
    if(color != vec3(-1, -1, -1)){
        gl_Position = modelViewProjectionMatrix * vec4(position, 1.0);
        vertexNormal = (modelMatrix * vec4(normal, 0.0)).xyz;
        vertexColor = color;
    }else{
        vec3 newPosition;
        vec3 newNormal;
        vec3 newColor;
        getTerrainValues(position, newPosition, newNormal, newColor);

        gl_Position = modelViewProjectionMatrix * vec4(newPosition, 1.0);
        vertexNormal = (modelMatrix * vec4(newNormal, 0.0)).xyz;
        vertexColor = newColor;
     }
  }