package com.dokar.arcmotiondemo

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.PathMeasure
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.dokar.arcmotiondemo.databinding.ActivityMainBinding
import kotlin.math.PI
import kotlin.math.atan2

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var animating = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val arcPathView = binding.arcPathView

        binding.seekMaxAngle.doOnProgressChanged {
            arcPathView.maxAngle = it.toFloat()
            binding.tvMaxAngleVal.text = "$it / 90"
        }
        binding.seekMaxAngle.progress = arcPathView.maxAngle.toInt()

        binding.minVerticalAngle.doOnProgressChanged {
            arcPathView.minVerticalAngle = it.toFloat()
            binding.tvMinVertAngleVal.text = "$it / 90"
        }
        binding.minVerticalAngle.progress = arcPathView.minVerticalAngle.toInt()

        binding.minHorizontalAngle.doOnProgressChanged {
            arcPathView.minHorizontalAngle = it.toFloat()
            binding.tvMinHoriAngleVal.text = "$it / 90"
        }
        binding.minHorizontalAngle.progress = arcPathView.minHorizontalAngle.toInt()
    }

    private fun go() {
        if (animating) {
            return
        }
        val arc = binding.arcPathView
        val path = arc.path ?: return
        val pm = PathMeasure(path, false)
        val pos = floatArrayOf(0f, 0f)
        val tan = floatArrayOf(0f, 0f)

        val sprite = binding.ivAirplane
        sprite.run {
            visibility = View.VISIBLE
            alpha = 0f
            scaleX = 0f
            scaleY = 0f
        }

        animating = true

        sprite.post {
            pm.getPosTan(0f, pos, tan)
            updateSprite(sprite, pos, tan)

            sprite.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .start()

            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 1200
                startDelay = 325
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener {
                    val value = it.animatedValue as Float
                    pm.getPosTan(pm.length * value, pos, tan)
                    updateSprite(sprite, pos, tan)
                }
                doOnEnd {
                    animating = false
                }
                start()
            }
        }
    }

    private fun updateSprite(sprite: View, pos: FloatArray, tan: FloatArray) {
        sprite.x = pos[0] - sprite.width / 2
        sprite.y = pos[1] - sprite.height / 2
        sprite.rotation = getSpriteAngle(tan)
    }

    private fun getSpriteAngle(tan: FloatArray): Float {
        return (180 * atan2(tan[1], tan[0]) / PI).toFloat() + 90f
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_go -> {
                go()
            }
        }
        return true
    }

}